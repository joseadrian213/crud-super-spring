package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    ISucursalService sucursalService;

    public SucursalController(ISucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalResponseDTO>> getSucursales() {
        return ResponseEntity.ok(sucursalService.traerSucursales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponseDTO> getSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.geSucursalDTO(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SucursalRequestDTO sucursalDTO) {
        SucursalResponseDTO created = sucursalService.crearSucursal(sucursalDTO);
        return ResponseEntity.created(URI.create("/api/sucursales/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SucursalRequestDTO sucursalDTO) {
        return ResponseEntity.ok(sucursalService.actualizarSucursal(id, sucursalDTO));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sucursalService.eliminarSucursal(id);
        return ResponseEntity.noContent().build();
    }

}
