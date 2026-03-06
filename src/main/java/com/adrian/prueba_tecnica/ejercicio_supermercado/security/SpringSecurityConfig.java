package com.adrian.prueba_tecnica.ejercicio_supermercado.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.adrian.prueba_tecnica.ejercicio_supermercado.security.filter.JwtAuthenticationFilter;
import com.adrian.prueba_tecnica.ejercicio_supermercado.security.filter.JwtValidationFilter;

/**
 * Clase de configuración centralizada para Spring Security.
 * 
 * Define toda la configuración de seguridad de la aplicación, incluyendo:
 * 
 * Cadena de filtros de seguridad HTTP
 * Reglas de autorización para endpoints
 * Autenticación JWT con filtros personalizados
 * Cifrado de contraseñas
 * Configuración CORS para solicitudes cross-origin
 * Deshabilitación de sesiones (stateless)
 * Protección contra CSRF
 * 
 * 
 * Autenticación:
 * 
 * Mecanismo: JWT (JSON Web Tokens)
 * Generación: {@link JwtAuthenticationFilter} en endpoint de login
 * Validación: {@link JwtValidationFilter} en cada petición
 * Duración: 1 hora
 * Almacenamiento: Encabezado Authorization con prefijo "Bearer"
 * 
 * 
 * Autorización:
 * 
 * Endpoints públicos: Login, registro, tickets, reportes
 * Endpoints protegidos: CRUD de productos, sucursales, ventas
 * Control de acceso: @PreAuthorize en métodos de controllers
 * 
 * 
 * CORS:
 * 
 * Orígenes permitidos: Todos (*)
 * Métodos HTTP: GET, POST, PUT, DELETE
 * Encabezados: Authorization, Content-Type
 * Credenciales: Habilitadas
 * 
 * 
 * Sesiones:
 * Deshabilitadas (STATELESS) para implementar un patrón de autenticación sin
 * estado
 * basado únicamente en tokens JWT.
 * 
 * @author Adrian
 * @version 1.0
 * @see JwtAuthenticationFilter
 * @see JwtValidationFilter
 * @see TokenJwtConfig
 */
// Clase con beans de configuracion y habilitamos la seguridad dentro de los
// metodos
@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    /**
     * Configuración de autenticación de Spring Security.
     * Se utiliza para obtener el AuthenticationManager de forma correcta.
     */
    // Necesario para obtener el AuthenticationManager de manera correcta
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    /**
     * Expone el AuthenticationManager como bean de Spring.
     * 
     * El AuthenticationManager es necesario para:
     * 
     * Validar credenciales de usuario en {@link JwtAuthenticationFilter}
     * Verificar password contra la base de datos
     * Cargar roles y autoridades del usuario
     * 
     * 
     * @return {@link AuthenticationManager} proporcionado por la configuración de
     *         autenticación
     * @throws Exception si ocurre error al obtener el AuthenticationManager
     */
    // Exponemos el AuthenticationManager como bean para que pueda ser usado por
    // nuestras clases de security
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Proporciona el codificador de contraseñas para la aplicación.
     * 
     * Las contraseñas de usuarios se almacenan codificadas en la base de datos
     * utilizando el algoritmo BCrypt, que es adaptativo e iterativo, haciendo
     * que sea computacionalmente costoso descifrar contraseñas.
     * 
     * Proceso:
     * 
     * Al registrar usuario: password se codifica con BCrypt antes de guardarse
     * Al hacer login: password ingresado se compara con hash almacenado
     * No se descodifica: es una comparación de hashes
     * 
     * 
     * @return {@link BCryptPasswordEncoder} configurado para codificar contraseñas
     */
    // Encriptamos las contraseñas
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define la cadena de filtros HTTP y las reglas de seguridad de la aplicación.
     * 
     * Configura:
     * 
     * Autorización de endpoints:
     * 
     * GET /api/user - Público (listar usuarios sin autenticación)
     * POST /api/user/register - Público (registrarse sin autenticación)
     * GET /api/ventas/ticket/* - Público (descargar tickets PDF)
     * GET /api/ventas/reporte/excel - Público (descargar reportes)
     * GET /api/ventas/reporte/excel/sucursal/** - Público (descargar reportes por
     * sucursal)
     * Cualquier otra petición - Requiere autenticación
     * 
     * Filtros JWT:
     * 
     * {@link JwtAuthenticationFilter} - Genera token al hacer login
     * {@link JwtValidationFilter} - Valida token en cada petición
     * 
     * Protecciones:
     * 
     * CSRF: Deshabilitado (no necesario en APIs REST stateless)
     * CORS: Habilitado con configuración personalizada
     * Sesiones: Deshabilitadas (STATELESS)
     * 
     * 
     * 
     * @param httpSecurity configuración HTTP de Spring Security
     * @return {@link SecurityFilterChain} cadena de filtros configurada
     * @throws Exception si ocurre error en la configuración
     * 
     * @see JwtAuthenticationFilter
     * @see JwtValidationFilter
     */
    // Definimos cuales seran las reglas de seguridad utilizando nuestras clases de
    // configuracion
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests((auth) -> auth.requestMatchers(HttpMethod.GET, "/api/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager())).csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(manegement -> manegement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * Configura las reglas CORS (Cross-Origin Resource Sharing).
     * 
     * CORS permite que navegadores y clientes JavaScript hagan peticiones
     * a orígenes diferentes al de la aplicación, bajo condiciones específicas.
     * 
     * Configuración:
     * 
     * Orígenes permitidos: Todos (*) - en producción ser más restrictivo
     * Métodos HTTP permitidos: GET, POST, PUT, DELETE
     * Encabezados permitidos: Authorization (para tokens JWT), Content-Type
     * Credenciales: Habilitadas (permite envío de cookies si fuera necesario)
     * 
     * 
     * Nota de seguridad:
     * En producción, se recomienda reemplazar "*" con orígenes específicos
     * permitidos:
     * 
     * config.setAllowedOrigins(Arrays.asList("https://miapp.com",
     * "https://www.miapp.com"));
     * 
     * 
     * @return {@link CorsConfigurationSource} con configuración de CORS para toda
     *         la aplicación
     */
    // Bean para la configuracion del CORS para saber el origen y tipo de peticiones
    // que se permiten o cabeceras
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;

    }

    /**
     * Registra el filtro CORS con máxima prioridad.
     * 
     * El filtro CORS debe ejecutarse antes que los filtros de Spring Security
     * para evitar conflictos en el procesamiento de peticiones CORS preflight
     * (OPTIONS).
     * 
     * Prioridad: HIGHEST_PRECEDENCE asegura que se ejecute primero.
     * </p>
     * 
     * @return {@link FilterRegistrationBean} configurando el CorsFilter con máxima
     *         prioridad
     */
    // Filtro del CORS en caso de conflicto con SpringSecurity para que se ejecute
    // antes que Security
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }

}