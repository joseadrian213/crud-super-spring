package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductoRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductoResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IProductoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST para la gestión de productos.
 * 
 * Proporciona endpoints para las operaciones CRUD (Create, Read, Update, Delete)
 * sobre productos. Implementa control de acceso basado en roles usando Spring Security.
 * 
 * Endpoints disponibles:
 * - GET /api/productos - Obtiene lista de todos los productos
 * - GET /api/productos/{id} - Obtiene un producto específico
 * - POST /api/productos - Crea un nuevo producto (solo ADMIN)
 * - PUT /api/productos/{id} - Actualiza un producto existente (solo ADMIN)
 * - DELETE /api/productos/{id} - Elimina un producto (solo ADMIN)
 * 
 * Control de acceso:
 * - Lectura (GET): Requiere roles ADMIN o USER
 * - Escritura (POST, PUT, DELETE): Requiere rol ADMIN
 * 
 * @author Adrian
 * @version 1.0
 * @see IProductoService
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    /**
     * Servicio para la gestión de lógica de negocio de productos.
     */
    private IProductoService productoService;

    /**
     * Constructor que inyecta el servicio de productos.
     * 
     * @param productoService servicio para gestionar la lógica de negocio de productos
     */
    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Obtiene la lista de todos los productos disponibles.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @return {@link ResponseEntity} con la lista de {@link ProductoResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<ProductoResponseDTO>> getProductos() {
        return ResponseEntity.ok(productoService.traerProductos());
    }

    /**
     * Obtiene un producto específico por su identificador.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @param id identificador único del producto a obtener
     * @return {@link ResponseEntity} con el {@link ProductoResponseDTO}
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si el producto no existe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ProductoResponseDTO> getProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoDTO(id));

    }

    /**
     * Crea un nuevo producto.
     * 
     * Requiere permisos: ADMIN
     * 
     * Valida los datos del producto usando anotaciones @Valid.
     * Si la creación es exitosa, retorna la ubicación del nuevo recurso
     * en el encabezado "Location".
     * 
     * @param productoDTO objeto {@link ProductoRequestDTO} con los datos del producto a crear
     * @return {@link ResponseEntity} con el {@link ProductoResponseDTO} creado
     *         con estado HTTP 201 (Created) y encabezado Location
     * @throws IllegalArgumentException si los datos del producto son inválidos
     * 
     * @see ProductoRequestDTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoRequestDTO productoDTO) {
        /*
         * El siguiente código se reemplaza por un método global para evitar repetir en
         * todos los controllers y quitar el parámetro BindingResult result
         * 
         * if (result.hasFieldErrors())
         *     return Validation.validation(result);
         */
        ProductoResponseDTO creado = productoService.creaProducto(productoDTO);
        return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(creado);
    }

    /**
     * Actualiza un producto existente.
     * 
     * Requiere permisos: ADMIN
     * 
     * Valida los datos del producto usando anotaciones @Valid.
     * Solo actualiza los campos proporcionados en el DTO.
     * 
     * @param id identificador único del producto a actualizar
     * @param productoDTO objeto {@link ProductoRequestDTO} con los datos a actualizar
     * @return {@link ResponseEntity} con el {@link ProductoResponseDTO} actualizado
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si el producto no existe
     * 
     * @see ProductoRequestDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO productoDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoDTO));
    }

    /**
     * Elimina un producto (desactiva) identificado por su ID.
     * 
     * Requiere permisos: ADMIN
     * 
     * Esta operación realiza una eliminación lógica: marca el producto como inactivo
     * en lugar de borrarlo físicamente de la base de datos.
     * 
     * @param id identificador único del producto a eliminar
     * @return {@link ResponseEntity} sin contenido con estado HTTP 204 (No Content)
     * @throws NotFoundException si el producto no existe
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();

    }
}
