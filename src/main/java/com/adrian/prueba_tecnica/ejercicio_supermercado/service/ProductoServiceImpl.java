package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductoRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductoResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Producto;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements IProductoService {

    private ProductoRepository repository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.repository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> traerProductos() {
        return repository.findAll().stream().map(Mapper::toDTO).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO getProductoDTO(Long id) {
        return repository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    @Override
    @Transactional
    public ProductoResponseDTO creaProducto(ProductoRequestDTO productoRequestDTO) {
        if (productoRequestDTO == null)
            throw new IllegalArgumentException("ProductoRequestDTO es null");

        if (productoRequestDTO.getNombre() == null || productoRequestDTO.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (productoRequestDTO.getPrecio() == null || productoRequestDTO.getPrecio().signum() < 0)
            throw new IllegalArgumentException("Precio inválido");

        if (productoRequestDTO.getCantidad() == null || productoRequestDTO.getCantidad() < 0)
            throw new IllegalArgumentException("Cantidad inválida");

        Producto prod = Producto.builder()
                .nombre(productoRequestDTO.getNombre())
                .categoria(productoRequestDTO.getCategoria())
                .precio(productoRequestDTO.getPrecio())
                .cantidad(productoRequestDTO.getCantidad())
                .activo(productoRequestDTO.getActivo())
                .build();
        return Mapper.toDTO(repository.save(prod));
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO productoRequestDTO) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        if (productoRequestDTO.getNombre() != null)
            producto.setNombre(productoRequestDTO.getNombre());

        if (productoRequestDTO.getCategoria() != null)
            producto.setCategoria(productoRequestDTO.getCategoria());

        if (productoRequestDTO.getCantidad() != null)
            producto.setCantidad(productoRequestDTO.getCantidad());

        if (productoRequestDTO.getPrecio() != null)
            producto.setPrecio(productoRequestDTO.getPrecio());

        if (productoRequestDTO.getActivo() != null)
            producto.setActivo(productoRequestDTO.getActivo());

        return Mapper.toDTO(repository.save(producto));

    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        producto.setActivo(false);

        repository.save(producto);

    }

}
