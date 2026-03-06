package com.adrian.prueba_tecnica.ejercicio_supermecado.service; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.user.UserRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.UserResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Role;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.RoleRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.UserRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.UserServiceImpl;

/**
 * Test unitario para UserServiceImpl.
 * 
 * Esta clase verifica el comportamiento del servicio de usuarios,
 * incluyendo operaciones de creación, búsqueda y gestión de roles.
 * 
 * @author Adrian
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private Role roleUser;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        // Configurar roles de prueba
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");

        roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setName("ROLE_ADMIN");

        // Configurar usuario de prueba
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setAdmin(false);
        user.setRoles(Arrays.asList(roleUser));

        // Configurar DTO de prueba
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("testuser");
        userRequestDTO.setPassword("password123");
        userRequestDTO.setEnabled(true);
        userRequestDTO.setAdmin(false);
    }

    @Test
    @DisplayName("Debería obtener todos los usuarios")
    void testFindAll() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("adminuser");
        user2.setPassword("encodedPassword");
        user2.setEnabled(true);
        user2.setAdmin(true);
        user2.setRoles(Arrays.asList(roleUser, roleAdmin));

        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserResponseDTO> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("adminuser", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un usuario por ID")
    void testFindById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserResponseDTO result = userService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getEnabled());
        assertFalse(result.getAdmin());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el usuario no existe")
    void testFindByIdNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            userService.findById(999L);
        });
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería crear un usuario normal exitosamente")
    void testSaveNormalUser() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDTO result = userService.save(userRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(roleRepository, never()).findByName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería crear un usuario administrador exitosamente")
    void testSaveAdminUser() {
        // Given
        userRequestDTO.setAdmin(true);

        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminuser");
        adminUser.setPassword("encodedPassword");
        adminUser.setEnabled(true);
        adminUser.setAdmin(true);
        adminUser.setRoles(Arrays.asList(roleUser, roleAdmin));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        // When
        UserResponseDTO result = userService.save(userRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("adminuser", result.getUsername());
        assertTrue(result.getAdmin());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando ROLE_USER no existe")
    void testSaveUserRoleNotFound() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.save(userRequestDTO);
        });
        assertEquals("ROLE_USER no existe", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando ROLE_ADMIN no existe para usuario admin")
    void testSaveAdminRoleNotFound() {
        // Given
        userRequestDTO.setAdmin(true);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.save(userRequestDTO);
        });
        assertEquals("ROLE_ADMIN no existe", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debería verificar que un usuario existe por nombre de usuario")
    void testExistByUsernameTrue() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        Boolean result = userService.existByUsername("testuser");

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    @DisplayName("Debería verificar que un usuario no existe por nombre de usuario")
    void testExistByUsernameFalse() {
        // Given
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When
        Boolean result = userService.existByUsername("nonexistent");

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername("nonexistent");
    }

    @Test
    @DisplayName("Debería codificar la contraseña al crear usuario")
    void testPasswordEncoding() {
        // Given
        String plainPassword = "myPassword123";
        String encodedPassword = "encodedMyPassword123";

        userRequestDTO.setPassword(plainPassword);

        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(encodedPassword, savedUser.getPassword());
            return savedUser;
        });

        // When
        userService.save(userRequestDTO);

        // Then
        verify(passwordEncoder, times(1)).encode(plainPassword);
    }

    @Test
    @DisplayName("Debería asignar solo ROLE_USER cuando admin es false")
    void testNonAdminUserRoles() {
        // Given
        userRequestDTO.setAdmin(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(1, savedUser.getRoles().size());
            assertTrue(savedUser.getRoles().contains(roleUser));
            return savedUser;
        });

        // When
        userService.save(userRequestDTO);

        // Then
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(roleRepository, never()).findByName("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Debería asignar ROLE_USER y ROLE_ADMIN cuando admin es true")
    void testAdminUserRoles() {
        // Given
        userRequestDTO.setAdmin(true);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(2, savedUser.getRoles().size());
            assertTrue(savedUser.getRoles().contains(roleUser));
            assertTrue(savedUser.getRoles().contains(roleAdmin));
            return savedUser;
        });

        // When
        userService.save(userRequestDTO);

        // Then
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
    }
}
