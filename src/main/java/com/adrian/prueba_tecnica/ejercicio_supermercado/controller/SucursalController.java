package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.SucursalRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.SucursalResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.ISucursalService;

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
 * @see ISucursalService
 */
@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    /**
     * Servicio para la gestión de lógica de negocio de sucursales.
     */
    ISucursalService sucursalService;

    /**
     * Constructor que inyecta el servicio de sucursales.
     * 
     * @param sucursalService servicio para gestionar la lógica de negocio de sucursales
     */
    public SucursalController(ISucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    /**
     * Obtiene la lista de todas las sucursales disponibles.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @return {@link ResponseEntity} con la lista de {@link SucursalResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<SucursalResponseDTO>> getSucursales() {
        return ResponseEntity.ok(sucursalService.traerSucursales());
    }

    /**
     * Obtiene una sucursal específica por su identificador.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @param id identificador único de la sucursal a obtener
     * @return {@link ResponseEntity} con el {@link SucursalResponseDTO}
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la sucursal no existe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<SucursalResponseDTO> getSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.geSucursalDTO(id));
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
     * @param sucursalDTO objeto {@link SucursalRequestDTO} con los datos de la sucursal a crear
     * @return {@link ResponseEntity} con el {@link SucursalResponseDTO} creado
     *         con estado HTTP 201 (Created) y encabezado Location
     * @throws IllegalArgumentException si los datos de la sucursal son inválidos
     * 
     * @see SucursalRequestDTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody SucursalRequestDTO sucursalDTO) {
        SucursalResponseDTO created = sucursalService.crearSucursal(sucursalDTO);
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
     * @param sucursalDTO objeto {@link SucursalRequestDTO} con los datos a actualizar
     * @return {@link ResponseEntity} con el {@link SucursalResponseDTO} actualizado
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la sucursal no existe
     * 
     * @see SucursalRequestDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SucursalRequestDTO sucursalDTO) {
        return ResponseEntity.ok(sucursalService.actualizarSucursal(id, sucursalDTO));

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
        sucursalService.eliminarSucursal(id);
        return ResponseEntity.noContent().build();
    }

}
