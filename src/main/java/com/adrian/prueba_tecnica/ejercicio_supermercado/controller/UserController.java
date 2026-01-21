package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.user.UserRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.UserResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IUserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controlador REST para la gestión de usuarios.
 * 
 * Este controlador proporciona endpoints para la administración de usuarios, incluyendo
 * operaciones de consulta (listar, obtener por ID) y registro de nuevos usuarios.
 * 
 * <p><strong>Rutas de acceso:</strong></p>
 * <ul>
 *   <li>GET /api/user - Obtiene la lista de todos los usuarios (ADMIN)
 *   <li>GET /api/user/{id} - Obtiene un usuario específico por su ID (ADMIN)
 *   <li>POST /api/user/register - Registra un nuevo usuario (público)
 * </ul>
 * 
 * <p><strong>Control de acceso:</strong></p>
 * Los endpoints GET requieren rol ADMIN. El endpoint de registro es público,
 * permitiendo a usuarios no autenticados crear nuevas cuentas.
 * 
 * @author Adrian
 * @version 1.0
 * @see IUserService
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    /**
     * Servicio para la gestión de lógica de negocio de usuarios.
     * Se utiliza para operaciones CRUD y consultas de usuarios.
     */
    private IUserService userService;

    /**
     * Constructor que inyecta el servicio de usuarios.
     * 
     * @param userService servicio para gestionar la lógica de negocio de usuarios
     */
    public UserController(IUserService userService) {
        this.userService = userService;

    }

    /**
     * Obtiene la lista de todos los usuarios registrados en el sistema.
     * 
     * Requiere permisos: ADMIN
     * 
     * Retorna una lista completa de objetos {@link UserResponseDTO} con la información
     * de todos los usuarios. Se realiza como consulta de solo lectura optimizando
     * el acceso a la base de datos.
     * 
     * @return {@link ResponseEntity} con la lista de {@link UserResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Obtiene un usuario específico por su identificador.
     * 
     * Requiere permisos: ADMIN
     * 
     * Busca y retorna los detalles completos del usuario incluyendo sus roles asignados.
     * 
     * @param id identificador único del usuario a obtener
     * @return {@link ResponseEntity} con el {@link UserResponseDTO} correspondiente
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si el usuario con el ID especificado no existe
     * 
     * @see UserResponseDTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Este endpoint es público y no requiere autenticación previa.
     * 
     * El servicio se encarga de:
     * <ul>
     *   <li>Validar que los datos del usuario sean correctos
     *   <li>Verificar que el nombre de usuario no esté en uso
     *   <li>Codificar la contraseña de forma segura
     *   <li>Asignar rol ROLE_USER de forma predeterminada
     *   <li>Opcionalmente asignar ROLE_ADMIN si admin=true en el DTO
     *   <li>Guardar el nuevo usuario en la base de datos
     * </ul>
     * 
     * @param userRequestDTO objeto {@link UserRequestDTO} con los datos del nuevo usuario
     *                       (username, password, email, admin)
     * @return {@link ResponseEntity} con el {@link UserResponseDTO} del usuario creado
     *         con estado HTTP 201 (Created)
     * @throws IllegalArgumentException si los datos del usuario son inválidos
     * @throws RuntimeException si el nombre de usuario ya existe en el sistema
     * 
     * @see UserRequestDTO
     * @see UserResponseDTO
     * @see IUserService#save(UserRequestDTO)
     */
    @PostMapping("/register")
    public ResponseEntity<?> create(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userRequestDTO));
    }

}
