package com.adrian.prueba_tecnica.ejercicio_supermercado.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase abstracta MixIn para la deserialización JSON de autoridades/roles de Spring Security.
 * 
 * Esta clase actúa como un "MixIn" de Jackson que proporciona instrucciones personalizadas
 * para la conversión de JSON a objetos {@link org.springframework.security.core.authority.SimpleGrantedAuthority}.
 * 
 * Propósito:
 * La clase {@link org.springframework.security.core.authority.SimpleGrantedAuthority} de Spring Security
 * no es directamente serializable/deserializable por Jackson por defecto. Este MixIn proporciona
 * las anotaciones necesarias para que Jackson pueda:
 * 
 *   Reconocer cómo crear instancias de SimpleGrantedAuthority desde JSON
 *   Mapear la propiedad JSON "authority" al parámetro del constructor
 *   Manejar la deserialización en arrays de autoridades
 * 
 * 
 * Caso de uso:
 * Se utiliza en {@link JwtValidationFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, 
 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)} para deserializar
 * los roles almacenados en formato JSON dentro del token JWT y convertirlos a objetos
 * que Spring Security pueda usar para evaluar permisos.
 * 
 * Ejemplo de uso:
 * 
 * ObjectMapper mapper = new ObjectMapper();
 * mapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class);
 * SimpleGrantedAuthority[] authorities = mapper.readValue(jsonBytes, SimpleGrantedAuthority[].class);
 * 
 * 
 * Estructura JSON esperada:
 * El JSON debe tener la estructura:
 * 
 * [
 *   {"authority": "ROLE_ADMIN"},
 *   {"authority": "ROLE_USER"}
 * ]
 * 
 * 
 * @author Adrian
 * @version 1.0
 * @see JwtValidationFilter
 * @see org.springframework.security.core.authority.SimpleGrantedAuthority
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
public abstract class SimpleGrantedAuthorityJsonCreator {
    
    /**
     * Constructor que instruye a Jackson sobre cómo deserializar JSON a SimpleGrantedAuthority.
     * 
     * La anotación {@link JsonCreator} indica que este constructor debe utilizarse
     * cuando Jackson deserialice objetos SimpleGrantedAuthority desde JSON.
     * 
     * La anotación {@link JsonProperty} especifica que la propiedad JSON "authority"
     * debe mapearse al parámetro "role" del constructor.
     * 
     * Nota:</strong> Este constructor nunca se ejecuta realmente, ya que esta clase
     * es solo un MixIn de instrucciones de Jackson. El constructor actual de SimpleGrantedAuthority
     * es el que se invoca, pero con la información de mapeo proporcionada por este MixIn.
     * 
     * @param role el nombre del rol/autoridad a deserializar desde la propiedad "authority" del JSON
     */
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
    }
}
