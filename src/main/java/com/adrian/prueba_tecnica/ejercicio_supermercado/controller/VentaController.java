package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IVentasService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private IVentasService ventasService;

    public VentaController(IVentasService ventasService) {
        this.ventasService = ventasService;
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> getVentas() {
        return ResponseEntity.ok(ventasService.traerVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> getVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.getVentaDTO(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VentaRequestDTO ventaDTO) {
        VentaResponseDTO created = ventasService.crearVenta(ventaDTO);
        return ResponseEntity.created(URI.create("/api/ventas/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody VentaUpdateRequestDTO ventaDTO) {
        // Actualiza fecha, estado, idSucursal, total
        // return ventasService.actualizarVenta(id, ventaDTO);
        return ResponseEntity.ok(ventasService.actualizarVenta(id, ventaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventasService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }

}
