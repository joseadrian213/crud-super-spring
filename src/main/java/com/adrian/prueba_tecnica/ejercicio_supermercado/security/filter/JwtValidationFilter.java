package com.adrian.prueba_tecnica.ejercicio_supermercado.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.adrian.prueba_tecnica.ejercicio_supermercado.security.SimpleGrantedAuthorityJsonCreator;
import com.adrian.prueba_tecnica.ejercicio_supermercado.security.TokenJwtConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de validación JWT para Spring Security.
 * 
 * Extiende {@link BasicAuthenticationFilter} para interceptar todas las peticiones HTTP
 * (excepto login) y validar que el token JWT sea válido antes de permitir acceso a los recursos protegidos.
 * 
 * <p><strong>Flujo de funcionamiento:</strong></p>
 * <ol>
 *   <li>Se ejecuta en cada petición HTTP hacia recursos protegidos
 *   <li>Verifica la presencia del encabezado Authorization con formato "Bearer {token}"
 *   <li>Extrae el token eliminando el prefijo "Bearer "
 *   <li>Valida la firma del token usando la clave secreta compartida
 *   <li>Si el token es válido:
 *       <ul>
 *         <li>Extrae el username del claim "subject"
 *         <li>Extrae y deserializa los roles/autoridades del claim "authorities"
 *         <li>Crea un objeto Authentication con el usuario y sus roles
 *         <li>Establece la autenticación en el SecurityContext
 *         <li>Permite que la petición continúe hacia el controlador
 *       </ul>
 *   <li>Si el token es inválido, expirado o mal formado:
 *       <ul>
 *         <li>Retorna error HTTP 401 (Unauthorized)
 *         <li>Envía respuesta JSON con descripción del error
 *       </ul>
 * </ol>
 * 
 * <p><strong>Validaciones realizadas:</strong></p>
 * <ul>
 *   <li>Presencia del encabezado Authorization
 *   <li>Prefijo correcto del token ("Bearer ")
 *   <li>Firma válida del token usando SECRET_KEY
 *   <li>Token no expirado
 *   <li>Claims bien formados
 * </ul>
 * 
 * <p><strong>Deserialización de autoridades:</strong></p>
 * Los roles se deserializan desde JSON utilizando un MixIn ({@link SimpleGrantedAuthorityJsonCreator})
 * que facilita la conversión desde formato JSON a objetos {@link SimpleGrantedAuthority}.
 * Esto es necesario porque los roles se almacenan como JSON en el token.
 * 
 * <p><strong>Ciclo de vida del token:</strong></p>
 * <ul>
 *   <li>Validez: 1 hora desde su emisión (configurado en JwtAuthenticationFilter)
 *   <li>Actualización: El cliente debe hacer login nuevamente cuando expira
 *   <li>Almacenamiento: Se envía en el header Authorization o en cookies
 * </ul>
 * 
 * @author Adrian
 * @version 1.0
 * @see JwtAuthenticationFilter
 * @see TokenJwtConfig
 * @see SimpleGrantedAuthorityJsonCreator
 * @see BasicAuthenticationFilter
 */
//Verifica que el token sea valido se ejecuta en cada request 
public class JwtValidationFilter extends BasicAuthenticationFilter {
    
    /**
     * Constructor que inyecta el gestor de autenticación de Spring Security.
     * 
     * @param authenticationManager gestor de autenticación utilizado por el filtro base
     */
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);

    }

    /**
     * Valida el token JWT incluido en la petición y establece la autenticación en el contexto de seguridad.
     * 
     * Este método se ejecuta internamente en cada petición HTTP después del login.
     * Verifica la validez del token y carga los roles del usuario en el contexto de seguridad
     * sin necesidad de consultar la base de datos nuevamente.
     * 
     * <p><strong>Proceso de validación:</strong></p>
     * <ol>
     *   <li>Obtiene el encabezado Authorization de la petición
     *   <li>Si el encabezado es nulo o no contiene el prefijo "Bearer ":
     *       <ul>
     *         <li>Continúa el flujo sin establecer autenticación (petición anónima)
     *         <li>Permite que el controlador maneje la autorización si es necesario
     *       </ul>
     *   <li>Si el encabezado es válido:
     *       <ul>
     *         <li>Extrae el token eliminando el prefijo
     *         <li>Valida la firma del token con la clave secreta
     *         <li>Extrae el username del claim "subject"
     *         <li>Extrae los roles del claim "authorities" (formato JSON)
     *         <li>Deserializa los roles a objetos SimpleGrantedAuthority
     *         <li>Crea un token de autenticación sin contraseña (no es necesaria)
     *         <li>Establece la autenticación en el SecurityContextHolder
     *         <li>Permite que la petición continúe hacia el controlador
     *       </ul>
     *   <li>Si ocurre error en validación de token:
     *       <ul>
     *         <li>Captura la excepción JwtException
     *         <li>Retorna respuesta JSON con error 401 (Unauthorized)
     *         <li>Incluye descripción del problema (token inválido, expirado, etc.)
     *       </ul>
     * </ol>
     * 
     * @param request la petición HTTP que contiene el token en el encabezado Authorization
     * @param response la respuesta HTTP
     * @param chain la cadena de filtros para continuar el flujo
     * @throws IOException si ocurre error de entrada/salida
     * @throws ServletException si ocurre error del servlet
     * 
     * @see TokenJwtConfig#HEADER_AUTHORIZATION
     * @see TokenJwtConfig#PREFIX_TOKEN
     * @see TokenJwtConfig#SECRET_KEY
     * @see SimpleGrantedAuthorityJsonCreator
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(TokenJwtConfig.HEADER_AUTHORIZATION);
        // Validamos cabeceras de validacion que no sean nulas y que tengan prefijo Bearer
        if (header == null || !header.startsWith(TokenJwtConfig.PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        // Quitamos el prefijo
        String token = header.replace(TokenJwtConfig.PREFIX_TOKEN, "");

        try {
            // Se valida el token
            Claims claims = Jwts.parser().verifyWith(TokenJwtConfig.SECRET_KEY).build().parseSignedClaims(token)
                    .getPayload();

            // Obtenemos el username del token
            String username = claims.getSubject();

            // Obtenemos los roles
            Object authoriesClaims = claims.get("authorities");

            // Deserializamos los authorities y los obtenemos con MixIn con la clase
            // auxiliar SimpleGrantedAuthorityJsonCreator
            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                    new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoriesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
            //Obtenemos el username y los roles con el token ya no se requiere el password 
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                    null, authorities);
            //Confirmamos que el usuario ya se encuentra autenticado 
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            
            //Permitimos pasar el request al controller 
            chain.doFilter(request, response);

        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es invalido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(TokenJwtConfig.CONTENT_TYPE);
        }
    }

}
