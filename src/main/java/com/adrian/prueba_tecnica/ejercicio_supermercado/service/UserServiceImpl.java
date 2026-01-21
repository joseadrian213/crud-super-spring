package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.user.UserRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.UserResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Role;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.RoleRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.UserRepository;

/**
 * Implementación del servicio de gestión de usuarios.
 * 
 * Esta clase proporciona la lógica de negocio para operaciones relacionadas con usuarios,
 * incluyendo búsqueda, creación de nuevos usuarios y asignación de roles.
 * Implementa la interfaz {@link IUserService}.
 * 
 * Características:
 * - Obtención de lista completa de usuarios
 * - Búsqueda de usuario por identificador
 * - Creación de nuevos usuarios con asignación de roles
 * - Verificación de existencia de usuario por nombre de usuario
 * - Codificación segura de contraseñas usando PasswordEncoder
 * - Asignación automática de roles basada en permisos administrativos
 * 
 * @author Adrian
 * @version 1.0
 * @see IUserService
 * @see User
 * @see Role
 */
@Service
public class UserServiceImpl implements IUserService {
    /**
     * Repositorio para acceder a la información de usuarios en la base de datos.
     */
    private UserRepository userRepository;

    /**
     * Repositorio para acceder a la información de roles en la base de datos.
     */
    private RoleRepository roleRespository;

    /**
     * Codificador de contraseñas para almacenarlas de forma segura.
     * Utiliza algoritmos de hash seguros para proteger las contraseñas.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param userRepository repositorio para acceder a datos de usuarios
     * @param roleRespository repositorio para acceder a datos de roles
     * @param passwordEncoder codificador de contraseñas
     */
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRespository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRespository = roleRespository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene la lista de todos los usuarios del sistema.
     * 
     * Este método busca todos los usuarios en la base de datos y los convierte
     * a objetos DTO para su devolución. Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link UserResponseDTO} con información de todos los usuarios
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    /**
     * Obtiene un usuario específico por su identificador.
     * 
     * @param id identificador único del usuario a buscar
     * @return {@link UserResponseDTO} con la información del usuario
     * @throws java.util.NoSuchElementException si el usuario no existe
     */
    @Override
    public UserResponseDTO findById(Long id) {
        return userRepository.findById(id).map(Mapper::toDTO).orElseThrow();
    }

    /**
     * Crea un nuevo usuario con los datos proporcionados.
     * 
     * Este método:
     * 1. Crea una nueva instancia de User con los datos del DTO
     * 2. Codifica la contraseña de forma segura usando PasswordEncoder
     * 3. Asigna el rol ROLE_USER de forma obligatoria
     * 4. Asigna el rol ROLE_ADMIN si el usuario tiene permisos administrativos
     * 5. Persiste el usuario en la base de datos
     * 6. Retorna el usuario creado como DTO
     * 
     * @param userRequestDTO objeto con los datos del usuario a crear
     * @return {@link UserResponseDTO} con los datos del usuario creado
     * @throws RuntimeException si los roles ROLE_USER o ROLE_ADMIN no existen en la base de datos
     * 
     * @see UserRequestDTO
     * @see PasswordEncoder
     */
    @Override
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {

        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        // Codificamos la contraseña de forma segura
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setEnabled(userRequestDTO.getEnabled());
        user.setAdmin(userRequestDTO.getAdmin());
        List<Role> roles = new ArrayList<>();

        // Asignamos el rol de usuario de forma obligatoria
        Role roleUser = roleRespository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER no existe"));
        roles.add(roleUser);

        // Si es administrador, asignamos el rol de admin
        if (Boolean.TRUE.equals(userRequestDTO.getAdmin())) {
            Role roleAdmin = roleRespository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN no existe"));
            roles.add(roleAdmin);
        }

        user.setRoles(roles);

        user = userRepository.save(user);

        // Forzamos la carga de roles (evita problemas con lazy loading)
        user.getRoles().size();

        return Mapper.toDTO(user);
    }

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     * 
     * @param username nombre de usuario a verificar
     * @return {@code true} si el usuario existe, {@code false} en caso contrario
     */
    @Override
    public Boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
