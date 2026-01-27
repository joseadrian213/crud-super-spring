package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductResponseDTO;

public interface IProductService {
    ProductResponseDTO findByIdProduct(Long id); 

    List<ProductResponseDTO> findAllProducts();

    ProductResponseDTO createProduct(ProductRequestDTO productDTO);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO);

    void deleteProduct(Long id);
}
