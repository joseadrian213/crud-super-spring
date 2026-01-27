package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.BranchRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.BranchResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IBranchService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST para la gestión de sucursales.
 * 
 * Proporciona endpoints para las operaciones CRUD (Create, Read, Update, Delete)
 * sobre sucursales. Implementa control de acceso basado en roles usando Spring Security.
 * 
 * Endpoints disponibles:
 * - GET /api/sucursales - Obtiene lista de todas las sucursales
 * - GET /api/sucursales/{id} - Obtiene una sucursal específica
 * - POST /api/sucursales - Crea una nueva sucursal (solo ADMIN)
 * - PUT /api/sucursales/{id} - Actualiza una sucursal existente (solo ADMIN)
 * - DELETE /api/sucursales/{id} - Elimina una sucursal (solo ADMIN)
 * 
 * Control de acceso:
 * - Lectura (GET): Requiere roles ADMIN o USER
 * - Escritura (POST, PUT, DELETE): Requiere rol ADMIN
 * 
 * @author Adrian
 * @version 1.0
 * @see IBranchService
 */
@RestController
@RequestMapping("/api/sucursales")
public class BranchController {

    /**
     * Servicio para la gestión de lógica de negocio de sucursales.
     */
    IBranchService branchService;

    /**
     * Constructor que inyecta el servicio de sucursales.
     * 
     * @param branchService servicio para gestionar la lógica de negocio de sucursales
     */
    public BranchController(IBranchService branchService) {
        this.branchService = branchService;
    }

    /**
     * Obtiene la lista de todas las sucursales disponibles.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @return {@link ResponseEntity} con la lista de {@link BranchResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<BranchResponseDTO>> findAllBranches() {
        return ResponseEntity.ok(branchService.findAllBranches());
    }

    /**
     * Obtiene una sucursal específica por su identificador.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @param id identificador único de la sucursal a obtener
     * @return {@link ResponseEntity} con el {@link BranchResponseDTO}
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la sucursal no existe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<BranchResponseDTO> findByIdBranch(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.findByIdBranch(id));
    }

    /**
     * Crea una nueva sucursal.
     * 
     * Requiere permisos: ADMIN
     * 
     * Valida los datos de la sucursal usando anotaciones @Valid.
     * Si la creación es exitosa, retorna la ubicación del nuevo recurso
     * en el encabezado "Location".
     * 
     * @param branchDTO objeto {@link BranchRequestDTO} con los datos de la sucursal a crear
     * @return {@link ResponseEntity} con el {@link BranchResponseDTO} creado
     *         con estado HTTP 201 (Created) y encabezado Location
     * @throws IllegalArgumentException si los datos de la sucursal son inválidos
     * 
     * @see BranchRequestDTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody BranchRequestDTO branchDTO) {
        BranchResponseDTO created = branchService.createBranch(branchDTO);
        return ResponseEntity.created(URI.create("/api/sucursales/" + created.getId())).body(created);
    }

    /**
     * Actualiza una sucursal existente.
     * 
     * Requiere permisos: ADMIN
     * 
     * Valida los datos de la sucursal usando anotaciones @Valid.
     * Solo actualiza los campos proporcionados en el DTO.
     * 
     * @param id identificador único de la sucursal a actualizar
     * @param branchDTO objeto {@link BranchRequestDTO} con los datos a actualizar
     * @return {@link ResponseEntity} con el {@link BranchResponseDTO} actualizado
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la sucursal no existe
     * 
     * @see BranchRequestDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BranchRequestDTO branchDTO) {
        return ResponseEntity.ok(branchService.updateBranch(id, branchDTO));

    }

    /**
     * Elimina una sucursal (desactiva) identificada por su ID.
     * 
     * Requiere permisos: ADMIN
     * 
     * Esta operación realiza una eliminación lógica: marca la sucursal como inactiva
     * en lugar de borrarla físicamente de la base de datos.
     * 
     * @param id identificador único de la sucursal a eliminar
     * @return {@link ResponseEntity} sin contenido con estado HTTP 204 (No Content)
     * @throws NotFoundException si la sucursal no existe
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

}
