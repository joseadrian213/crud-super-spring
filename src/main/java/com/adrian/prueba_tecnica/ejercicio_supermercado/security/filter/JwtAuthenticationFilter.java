package com.adrian.prueba_tecnica.ejercicio_supermercado.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;
import com.adrian.prueba_tecnica.ejercicio_supermercado.security.TokenJwtConfig;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación JWT basado en Spring Security.
 * 
 * Extiende {@link UsernamePasswordAuthenticationFilter} para interceptar peticiones de login
 * y generar tokens JWT (JSON Web Tokens) cuando la autenticación es exitosa.
 * 
 * Flujo de funcionamiento:
 * 
 *   Usuario envía credenciales (username, password) al endpoint de login
 *   Se invoca {@link #attemptAuthentication(HttpServletRequest, HttpServletResponse)}
 *   Se validan las credenciales contra la base de datos mediante {@link AuthenticationManager}
 *   Si la validación es correcta:
 *       
 *         Se invoca {@link #successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}
 *         Se genera un token JWT con username, roles y fecha de expiración
 *         El token se devuelve en el encabezado Authorization y en el cuerpo JSON
 *       
 *   Si la validación falla:
 *       
 *         Se invoca {@link #unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException)}
 *         Se retorna un error 401 (Unauthorized) con descripción del problema
 *       
 * 
 * 
 * Token JWT:
 * El token incluye los siguientes claims:
 * 
 *   subject: username del usuario autenticado
 *   authorities: lista de roles/permisos en formato JSON
 *   usename: username repetido en claims (nota: contiene typo)
 *   expiration: timestamp de expiración (1 hora desde emisión)
 *   issuedAt: timestamp de emisión del token
 * 
 * 
 * Configuración de seguridad:
 * La clase utiliza configuración desde {@link TokenJwtConfig} para:
 * 
 *   SECRET_KEY: Clave secreta para firmar y validar tokens
 *   HEADER_AUTHORIZATION: Nombre del encabezado HTTP (Authorization)
 *   PREFIX_TOKEN: Prefijo del token en el encabezado (Bearer)
 *   CONTENT_TYPE: Tipo de contenido de respuesta (application/json)
 * 
 * 
 * @author Adrian
 * @version 1.0
 * @see TokenJwtConfig
 * @see AuthenticationManager
 * @see UsernamePasswordAuthenticationFilter
 */
//Se encarga de validar los datos de usuario y si es correcto genera el JWT o Token
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    /**
     * Logger para registrar eventos del filtro de autenticación.
     */
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * Gestor de autenticación proporcionado por Spring Security.
     * Se utiliza para validar las credenciales del usuario contra la base de datos.
     */
    private AuthenticationManager authenticationManager;

    /**
     * Constructor que inyecta el gestor de autenticación.
     * 
     * @param authenticationManager gestor que se utiliza para autenticar usuarios
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Intenta autenticar al usuario extrayendo sus credenciales de la petición HTTP.
     * 
     * Este método se invoca automáticamente cuando se recibe una solicitud POST al endpoint
     * de login (configurado en la cadena de filtros de seguridad).
     * 
     * Proceso:
     * 
     *   Lee el cuerpo de la petición como JSON
     *   Extrae username y password de la entidad User
     *   Crea un token de autenticación sin roles (serán añadidos después)
     *   Delega la validación al AuthenticationManager
     * 
     * 
     * Manejo de excepciones:
     * Las excepciones de lectura JSON se capturan e imprimen en stack trace,
     * pero no interrumpen el flujo (comportamiento actual).
     * 
     * @param request la petición HTTP que contiene las credenciales en formato JSON
     * @param response la respuesta HTTP
     * @return {@link Authentication} con el usuario autenticado y sus roles/autoridades
     * @throws AuthenticationException si la autenticación falla (usuario no existe o password incorrecto)
     * 
     * @see #successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)
     * @see AuthenticationManager#authenticate(Authentication)
     */
    // Se crea cuando se inicia sesion
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        User user = null;
        String username = null;
        String password = null;
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
           
            username = user.getUsername();
            password = user.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creamos el token con pass y user sin roles
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        // Se encarga de validacion y llamar a _UserDetails, verificar roles y comparar password
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * Método invocado cuando la autenticación es exitosa.
     * 
     * Genera un token JWT con los datos del usuario y lo devuelve en la respuesta HTTP,
     * tanto en los encabezados como en el cuerpo JSON.
     * 
     * Proceso:
     * 
     *   Extrae el usuario y sus roles del objeto Authentication
     *   Crea un Claims con username y roles serializados a JSON
     *   Construye el token JWT con:
     *       
     *         Subject: username del usuario
     *         Claims: roles y datos adicionales
     *         Expiración: 1 hora desde la emisión (3600000 ms)
     *         Firma: se firma con la clave secreta configurada
     *       
     *   Devuelve el token en encabezado Authorization con prefijo Bearer
     *   Devuelve también un JSON con token, username y mensaje de éxito
     * 
     * 
     * @param request la petición HTTP original
     * @param response la respuesta HTTP donde se añadirán encabezados y contenido
     * @param chain la cadena de filtros (no se utiliza en este caso)
     * @param authResult objeto Authentication con el usuario y sus autoridades
     * @throws IOException si ocurre error escribiendo la respuesta
     * @throws ServletException si ocurre error del servlet
     * 
     * @see TokenJwtConfig
     */
    //Se ejecuta solo cuando el login es correcto 
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        
        //obtenemos el username de spring no de entidad  
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult
                .getPrincipal();

        String username = user.getUsername();
        
        //Obtenemos los roles 
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        
        //Creacion de claims que iran dentro del token username y roles  
        Claims claims = Jwts.claims().add("authorities", new ObjectMapper().writeValueAsString(roles))
                .add("usename", username).build();
        
        //Creamos token 
        String token = Jwts.builder().subject(username).claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 3600000)).issuedAt(new Date())
                .signWith(TokenJwtConfig.SECRET_KEY)
                .compact();
        
        //Enviamos a las cabeceras del cliente 
        response.addHeader(TokenJwtConfig.HEADER_AUTHORIZATION, TokenJwtConfig.PREFIX_TOKEN + token);

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("username", username);
        body.put("message", String.format("Has iniciado %s sesion con exito", username));
        
        //Devolvemos json al cliente 
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(TokenJwtConfig.CONTENT_TYPE);
        response.setStatus(200);
    }

    /**
     * Método invocado cuando la autenticación falla.
     * 
     * Devuelve una respuesta HTTP 401 (Unauthorized) con un mensaje de error en JSON
     * que describe el motivo del fallo de autenticación.
     * 
     * Posibles causas de fallo:
     * 
     *   Username no existe en la base de datos
     *   Password incorrecto para el usuario
     *   Usuario deshabilitado (enabled=false)
     * 
     * 
     * Respuesta devuelta:
     * JSON con campos:
     * 
     *   message: "Error en la autenticacion username o password incorrecto"
     *   error: mensaje de excepción detallado
     *   HTTP Status: 401 Unauthorized
     * 
     * 
     * @param request la petición HTTP original
     * @param response la respuesta HTTP donde se envía el error
     * @param failed la excepción de autenticación que describe el motivo del fallo
     * @throws IOException si ocurre error escribiendo la respuesta
     * @throws ServletException si ocurre error del servlet
     * 
     * @see AuthenticationException
     */
    //Si la autenticacion es incorrecta
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrecto");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(TokenJwtConfig.CONTENT_TYPE);
    }

}