package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> getProductos() {
        return ResponseEntity.ok(productoService.traerProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> getProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoDTO(id));

    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoRequestDTO productoDTO) {
        /*
         * El siguiente codigo se remplaza por un metodo global para evitar repetir en
         * todos los controllers
         * codigo y quitar el parametro BindingResult result
         * if (result.hasFieldErrors())
         * return Validation.validation(result);
         */
        ProductoResponseDTO creado = productoService.creaProducto(productoDTO);
        return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO productoDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();

    }
}
