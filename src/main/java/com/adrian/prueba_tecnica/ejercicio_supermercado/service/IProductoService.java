package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductoRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductoResponseDTO;

public interface IProductoService {
    ProductoResponseDTO getProductoDTO(Long id); 

    List<ProductoResponseDTO> traerProductos();

    ProductoResponseDTO creaProducto(ProductoRequestDTO productoDTO);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO productoDTO);

    void eliminarProducto(Long id);
}
