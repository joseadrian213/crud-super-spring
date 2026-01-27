package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IProductService;

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
 * Proporciona endpoints para las operaciones CRUD (Create, Read, Update,
 * Delete)
 * sobre productos. Implementa control de acceso basado en roles usando Spring
 * Security.
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
 * @see IProductService
 */
@RestController
@RequestMapping("/api/productos")
public class ProductController {

    /**
     * Servicio para la gestión de lógica de negocio de productos.
     */
    private IProductService productService;

    /**
     * Constructor que inyecta el servicio de productos.
     * 
     * @param productService servicio para gestionar la lógica de negocio de
     *                        productos
     */
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    /**
     * Obtiene la lista de todos los productos disponibles.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @return {@link ResponseEntity} con la lista de {@link ProductResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<ProductResponseDTO>> findAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    /**
     * Obtiene un producto específico por su identificador.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * @param id identificador único del producto a obtener
     * @return {@link ResponseEntity} con el {@link ProductResponseDTO}
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si el producto no existe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ProductResponseDTO> findByIdProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findByIdProduct(id));

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
     * @param productDTO objeto {@link ProductRequestDTO} con los datos del
     *                    producto a crear
     * @return {@link ResponseEntity} con el {@link ProductResponseDTO} creado
     *         con estado HTTP 201 (Created) y encabezado Location
     * @throws IllegalArgumentException si los datos del producto son inválidos
     * 
     * @see ProductRequestDTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDTO productDTO) {
        /*
         * El siguiente código se reemplaza por un método global para evitar repetir en
         * todos los controllers y quitar el parámetro BindingResult result
         * 
         * if (result.hasFieldErrors())
         * return Validation.validation(result);
         */
        ProductResponseDTO created = productService.createProduct(productDTO);
        return ResponseEntity.created(URI.create("/api/productos/" + created.getId())).body(created);
    }

    /**
     * Actualiza un producto existente.
     * 
     * Requiere permisos: ADMIN
     * 
     * Valida los datos del producto usando anotaciones @Valid.
     * Solo actualiza los campos proporcionados en el DTO.
     * 
     * @param id          identificador único del producto a actualizar
     * @param productDTO objeto {@link ProductRequestDTO} con los datos a
     *                    actualizar
     * @return {@link ResponseEntity} con el {@link ProductResponseDTO} actualizado
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si el producto no existe
     * 
     * @see ProductRequestDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    /**
     * Elimina un producto (desactiva) identificado por su ID.
     * 
     * Requiere permisos: ADMIN
     * 
     * Esta operación realiza una eliminación lógica: marca el producto como
     * inactivo
     * en lugar de borrarlo físicamente de la base de datos.
     * 
     * @param id identificador único del producto a eliminar
     * @return {@link ResponseEntity} sin contenido con estado HTTP 204 (No Content)
     * @throws NotFoundException si el producto no existe
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();

    }
}
