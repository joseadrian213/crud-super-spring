package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;

public interface IVentasService {

    VentaResponseDTO getVentaDTO(Long id); 
    
    List<VentaResponseDTO> traerVentas();

    VentaResponseDTO crearVenta(VentaRequestDTO ventaDTO);

    VentaResponseDTO actualizarVenta(Long id, VentaUpdateRequestDTO ventaDTO);

    void eliminarVenta(Long id);
}
