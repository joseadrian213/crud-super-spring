package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.UserRepository;

/**
 * Servicio que implementa la lógica de carga de detalles de usuario para autenticación.
 * 
 * Esta clase es utilizada por Spring Security para buscar información de usuarios
 * en la base de datos y construir los detalles de usuario necesarios para el proceso
 * de autenticación. Implementa la interfaz {@link UserDetailsService} de Spring Security.
 * 
 * El servicio se encarga de:
 * - Buscar usuarios por nombre de usuario
 * - Recuperar los roles asociados al usuario
 * - Convertir los roles a autoridades reconocidas por Spring Security
 * - Construir un objeto {@link UserDetails} con la información de seguridad
 * 
 * @author Adrian
 * @version 1.0
 * @see UserDetailsService
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    /**
     * Repositorio para acceder a la información de usuarios en la base de datos.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Carga los detalles de un usuario específico por su nombre de usuario.
     * 
     * Este método es invocado por el {@code AuthenticationManager} durante el proceso
     * de autenticación. Busca el usuario en la base de datos utilizando su nombre de usuario,
     * recupera sus roles asociados, y construye un objeto {@link UserDetails} que contiene
     * las credenciales y autoridades necesarias para Spring Security.
     * 
     * @param username el nombre de usuario a buscar en el sistema
     * @return un objeto {@link UserDetails} con la información del usuario y sus autoridades
     * @throws UsernameNotFoundException si el usuario no existe en el sistema
     * 
     * @see UserDetailsService#loadUserByUsername(String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Buscamos al usuario en la base de datos con sus roles
        Optional<User> userOptional = userRepository.findByUsernameWhitRoles(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema!", username));
        }
        
        // Obtenemos el usuario y convertimos sus roles a Authorities de Spring Security
        User user = userOptional.orElseThrow();
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Construimos el UserDetails que Spring Security necesita para autenticación
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(),
                user.getEnabled(), 
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }

}
