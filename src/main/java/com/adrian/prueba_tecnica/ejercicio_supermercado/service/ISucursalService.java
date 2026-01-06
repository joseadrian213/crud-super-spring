package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.sucursal.SucursalRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.SucursalResponseDTO;


public interface ISucursalService {
    
    List<SucursalResponseDTO> traerSucursales();
    
    SucursalResponseDTO geSucursalDTO(Long id); 
    
    SucursalResponseDTO crearSucursal(SucursalRequestDTO sucursalDTO);

    SucursalResponseDTO actualizarSucursal(Long id, SucursalRequestDTO sucursalDTO);

    void eliminarSucursal(Long id);
}
