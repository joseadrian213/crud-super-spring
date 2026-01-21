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

/**
 * Implementación del servicio de gestión de productos.
 * 
 * Esta clase proporciona la lógica de negocio para operaciones CRUD sobre productos,
 * incluyendo validaciones, búsqueda, creación, actualización y eliminación (lógica).
 * Implementa la interfaz {@link IProductoService}.
 * 
 * Características:
 * - Obtención de lista de productos activos
 * - Búsqueda de producto por identificador
 * - Creación de nuevos productos con validaciones
 * - Actualización parcial de información de productos
 * - Eliminación lógica de productos (marcado como inactivo)
 * 
 * @author Adrian
 * @version 1.0
 * @see IProductoService
 * @see Producto
 */
@Service
public class ProductoServiceImpl implements IProductoService {

    /**
     * Repositorio para acceder a la información de productos en la base de datos.
     */
    private ProductoRepository repository;

    /**
     * Constructor que inyecta el repositorio de productos.
     * 
     * @param productoRepository repositorio para acceder a datos de productos
     */
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.repository = productoRepository;
    }

    /**
     * Obtiene la lista de todos los productos disponibles.
     * 
     * Este método busca todos los productos en la base de datos y los convierte
     * a objetos DTO para su devolución. Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link ProductoResponseDTO} con información de todos los productos
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> traerProductos() {
        return repository.findAll().stream().map(Mapper::toDTO).toList();

    }

    /**
     * Obtiene un producto específico por su identificador.
     * 
     * @param id identificador único del producto a buscar
     * @return {@link ProductoResponseDTO} con la información del producto
     * @throws NotFoundException si el producto no existe en la base de datos
     */
    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO getProductoDTO(Long id) {
        return repository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    /**
     * Crea un nuevo producto con los datos proporcionados.
     * 
     * Valida que:
     * - El DTO de solicitud no sea nulo
     * - El nombre sea obligatorio y no esté en blanco
     * - El precio sea válido y no negativo
     * - La cantidad sea válida y no negativa
     * 
     * @param productoRequestDTO objeto con los datos del producto a crear
     * @return {@link ProductoResponseDTO} con los datos del producto creado
     * @throws IllegalArgumentException si alguna validación falla
     * 
     * @see ProductoRequestDTO
     */
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

    /**
     * Actualiza la información de un producto existente.
     * 
     * Solo actualiza los campos que son proporcionados en el DTO de solicitud.
     * Si un campo es nulo, no se modifica.
     * 
     * @param id identificador único del producto a actualizar
     * @param productoRequestDTO objeto con los datos del producto a actualizar
     * @return {@link ProductoResponseDTO} con los datos del producto actualizado
     * @throws NotFoundException si el producto no existe en la base de datos
     * 
     * @see ProductoRequestDTO
     */
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

    /**
     * Elimina (desactiva) un producto específico.
     * 
     * Esta operación realiza una eliminación lógica: marca el producto como inactivo
     * en lugar de borrarlo físicamente de la base de datos.
     * 
     * @param id identificador único del producto a eliminar
     * @throws NotFoundException si el producto no existe en la base de datos
     */
    @Override
    @Transactional
    public void eliminarProducto(Long id) {

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        producto.setActivo(false);

        repository.save(producto);

    }

}
