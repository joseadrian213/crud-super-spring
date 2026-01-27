package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.BranchRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.BranchResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Branch;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.BranchRepository;

/**
 * Implementación del servicio de gestión de sucursales.
 * 
 * Esta clase proporciona la lógica de negocio para operaciones CRUD sobre sucursales,
 * incluyendo validaciones, búsqueda, creación, actualización y eliminación (lógica).
 * Implementa la interfaz {@link IBranchService}.
 * 
 * Características:
 * - Obtención de lista de sucursales activas
 * - Búsqueda de sucursal por identificador
 * - Creación de nuevas sucursales con validaciones
 * - Actualización parcial de información de sucursales
 * - Eliminación lógica de sucursales (marcado como inactivo)
 * 
 * @author Adrian
 * @version 1.0
 * @see IBranchService
 * @see Branch
 */
@Service
public class BranchServiceImpl implements IBranchService {
    /**
     * Repositorio para acceder a la información de sucursales en la base de datos.
     */
    private BranchRepository repository;

    /**
     * Constructor que inyecta el repositorio de sucursales.
     * 
     * @param branchRepository repositorio para acceder a datos de sucursales
     */
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.repository = branchRepository;
    }

    /**
     * Obtiene la lista de todas las sucursales disponibles.
     * 
     * Este método busca todas las sucursales en la base de datos y las convierte
     * a objetos DTO para su devolución. Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link BranchResponseDTO} con información de todas las sucursales
     */
    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDTO> findAllBranches() {
        return repository.findAll().stream().map(Mapper::toDTO).toList();

    }

    /**
     * Obtiene una sucursal específica por su identificador.
     * 
     * @param id identificador único de la sucursal a buscar
     * @return {@link BranchResponseDTO} con la información de la sucursal
     * @throws NotFoundException si la sucursal no existe en la base de datos
     */
    @Override
    @Transactional(readOnly = true)
    public BranchResponseDTO findByIdBranch(Long id) {
        return repository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
    }

    /**
     * Crea una nueva sucursal con los datos proporcionados.
     * 
     * Valida que:
     * - El DTO de solicitud no sea nulo
     * - El nombre sea obligatorio y no esté en blanco
     * - La dirección sea obligatoria y no esté en blanco
     * 
     * @param branchRequestDTO objeto con los datos de la sucursal a crear
     * @return {@link BranchResponseDTO} con los datos de la sucursal creada
     * @throws IllegalArgumentException si alguna validación falla
     * 
     * @see BranchRequestDTO
     */
    @Override
    @Transactional
    public BranchResponseDTO createBranch(BranchRequestDTO branchRequestDTO) {
        if (branchRequestDTO == null)
            throw new IllegalArgumentException("branchRequestDTO es null");

        if (branchRequestDTO.getName() == null || branchRequestDTO.getName().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (branchRequestDTO.getAddress() == null || branchRequestDTO.getAddress().isBlank())
            throw new IllegalArgumentException("La direccion es obligatoria");

        Branch branch = Branch.builder()
                .name(branchRequestDTO.getName())
                .address(branchRequestDTO.getAddress())
                .active(branchRequestDTO.getActive())
                .build();
        return Mapper.toDTO(repository.save(branch));
    }

    /**
     * Actualiza la información de una sucursal existente.
     * 
     * Solo actualiza los campos que son proporcionados en el DTO de solicitud.
     * Si un campo es nulo, no se modifica.
     * 
     * @param id identificador único de la sucursal a actualizar
     * @param branchRequestDTO objeto con los datos de la sucursal a actualizar
     * @return {@link BranchResponseDTO} con los datos de la sucursal actualizada
     * @throws NotFoundException si la sucursal no existe en la base de datos
     * 
     * @see BranchRequestDTO
     */
    @Override
    @Transactional
    public BranchResponseDTO updateBranch(Long id, BranchRequestDTO branchRequestDTO) {
        Branch branch = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("branch no encontrada"));

        if (branchRequestDTO.getName() != null)
            branch.setName(branchRequestDTO.getName());

        if (branchRequestDTO.getAddress() != null)
            branch.setAddress(branchRequestDTO.getAddress());

        if (branchRequestDTO.getActive() != null)
            branch.setActive(branchRequestDTO.getActive());
        
        return Mapper.toDTO(repository.save(branch));
    }

    /**
     * Elimina (desactiva) una sucursal específica.
     * 
     * Esta operación realiza una eliminación lógica: marca la sucursal como inactiva
     * en lugar de borrarla físicamente de la base de datos.
     * 
     * @param id identificador único de la sucursal a eliminar
     * @throws NotFoundException si la sucursal no existe en la base de datos
     */
    @Override
    @Transactional
    public void deleteBranch(Long id) {

        Branch branch = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("branch no encontrada"));
        branch.setActive(false);
        repository.save(branch);

    }

}
