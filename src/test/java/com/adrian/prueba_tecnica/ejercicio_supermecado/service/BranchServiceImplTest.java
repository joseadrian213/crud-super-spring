package com.adrian.prueba_tecnica.ejercicio_supermecado.service; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.BranchRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.BranchResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Branch;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.BranchRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.BranchServiceImpl;

/**
 * Test unitario para BranchServiceImpl.
 * 
 * Esta clase verifica el comportamiento del servicio de sucursales,
 * incluyendo operaciones CRUD y validaciones de negocio.
 * 
 * @author Adrian
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de BranchServiceImpl")
class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchServiceImpl branchService;

    private Branch branch;
    private BranchRequestDTO branchRequestDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        branch = Branch.builder()
                .id(1L)
                .name("Sucursal Centro")
                .address("Av. Principal #123, Centro")
                .active(true)
                .build();

        branchRequestDTO = new BranchRequestDTO();
        branchRequestDTO.setName("Sucursal Centro");
        branchRequestDTO.setAddress("Av. Principal #123, Centro");
        branchRequestDTO.setActive(true);
    }

    @Test
    @DisplayName("Debería obtener todas las sucursales")
    void testFindAllBranches() {
        // Given
        Branch branch2 = Branch.builder()
                .id(2L)
                .name("Sucursal Norte")
                .address("Av. Norte #456, Zona Norte")
                .active(true)
                .build();

        List<Branch> branches = Arrays.asList(branch, branch2);
        when(branchRepository.findAll()).thenReturn(branches);

        // When
        List<BranchResponseDTO> result = branchService.findAllBranches();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Sucursal Centro", result.get(0).getName());
        assertEquals("Sucursal Norte", result.get(1).getName());
        verify(branchRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una sucursal por ID")
    void testFindByIdBranch() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));

        // When
        BranchResponseDTO result = branchService.findByIdBranch(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sucursal Centro", result.getName());
        assertEquals("Av. Principal #123, Centro", result.getAddress());
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException cuando la sucursal no existe")
    void testFindByIdBranchNotFound() {
        // Given
        when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            branchService.findByIdBranch(999L);
        });
        verify(branchRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería crear una sucursal exitosamente")
    void testCreateBranch() {
        // Given
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        // When
        BranchResponseDTO result = branchService.createBranch(branchRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Sucursal Centro", result.getName());
        assertEquals("Av. Principal #123, Centro", result.getAddress());
        assertTrue(result.isActive());
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el BranchRequestDTO es nulo")
    void testCreateBranchWithNullDTO() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            branchService.createBranch(null);
        });
        assertEquals("branchRequestDTO es null", exception.getMessage());
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el nombre es nulo")
    void testCreateBranchWithNullName() {
        // Given
        branchRequestDTO.setName(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            branchService.createBranch(branchRequestDTO);
        });
        assertEquals("El nombre es obligatorio", exception.getMessage());
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el nombre está vacío")
    void testCreateBranchWithBlankName() {
        // Given
        branchRequestDTO.setName("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            branchService.createBranch(branchRequestDTO);
        });
        assertEquals("El nombre es obligatorio", exception.getMessage());
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la dirección es nula")
    void testCreateBranchWithNullAddress() {
        // Given
        branchRequestDTO.setAddress(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            branchService.createBranch(branchRequestDTO);
        });
        assertEquals("La direccion es obligatoria", exception.getMessage());
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la dirección está vacía")
    void testCreateBranchWithBlankAddress() {
        // Given
        branchRequestDTO.setAddress("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            branchService.createBranch(branchRequestDTO);
        });
        assertEquals("La direccion es obligatoria", exception.getMessage());
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería actualizar una sucursal exitosamente")
    void testUpdateBranch() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        BranchRequestDTO updateDTO = new BranchRequestDTO();
        updateDTO.setName("Sucursal Centro Actualizada");
        updateDTO.setAddress("Av. Principal #456, Centro");

        // When
        BranchResponseDTO result = branchService.updateBranch(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(branchRepository, times(1)).findById(1L);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería actualizar solo los campos proporcionados")
    void testUpdateBranchPartial() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        BranchRequestDTO updateDTO = new BranchRequestDTO();
        updateDTO.setName("Sucursal Centro Actualizada");
        // No se actualizan otros campos

        // When
        BranchResponseDTO result = branchService.updateBranch(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(branchRepository, times(1)).findById(1L);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException al actualizar sucursal inexistente")
    void testUpdateBranchNotFound() {
        // Given
        when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());

        BranchRequestDTO updateDTO = new BranchRequestDTO();
        updateDTO.setName("Sucursal Nueva");

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            branchService.updateBranch(999L, updateDTO);
        });
        verify(branchRepository, times(1)).findById(999L);
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debería eliminar (desactivar) una sucursal exitosamente")
    void testDeleteBranch() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        // When
        branchService.deleteBranch(1L);

        // Then
        assertFalse(branch.isActive());
        verify(branchRepository, times(1)).findById(1L);
        verify(branchRepository, times(1)).save(branch);
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException al eliminar sucursal inexistente")
    void testDeleteBranchNotFound() {
        // Given
        when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            branchService.deleteBranch(999L);
        });
        verify(branchRepository, times(1)).findById(999L);
        verify(branchRepository, never()).save(any(Branch.class));
    }
}
