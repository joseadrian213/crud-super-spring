package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;

public interface ISaleService {

    SaleResponseDTO findByIdSale(Long id); 
    
    List<SaleResponseDTO> findAllSales();

    SaleResponseDTO createSale(SaleRequestDTO saleDTO);

    SaleResponseDTO updateSale(Long id, SaleUpdateRequestDTO saleDTO);

    void deleteSale(Long id);
}
