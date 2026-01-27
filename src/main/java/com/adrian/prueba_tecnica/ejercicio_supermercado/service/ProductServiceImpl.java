package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Product;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.ProductRepository;

/**
 * Implementación del servicio de gestión de productos.
 * 
 * Esta clase proporciona la lógica de negocio para operaciones CRUD sobre
 * productos,
 * incluyendo validaciones, búsqueda, creación, actualización y eliminación
 * (lógica).
 * Implementa la interfaz {@link IProductService}.
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
 * @see IProductService
 * @see Product
 */
@Service
public class ProductServiceImpl implements IProductService {

    /**
     * Repositorio para acceder a la información de productos en la base de datos.
     */
    private ProductRepository repository;

    /**
     * Constructor que inyecta el repositorio de productos.
     * 
     * @param productRepository repositorio para acceder a datos de productos
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.repository = productRepository;
    }

    /**
     * Obtiene la lista de todos los productos disponibles.
     * 
     * Este método busca todos los productos en la base de datos y los convierte
     * a objetos DTO para su devolución. Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link ProductResponseDTO} con información de todos los
     *         productos
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllProducts() {
        return repository.findAll().stream().map(Mapper::toDTO).toList();

    }

    /**
     * Obtiene un producto específico por su identificador.
     * 
     * @param id identificador único del producto a buscar
     * @return {@link ProductResponseDTO} con la información del producto
     * @throws NotFoundException si el producto no existe en la base de datos
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findByIdProduct(Long id) {
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
     * @param productRequestDTO objeto con los datos del producto a crear
     * @return {@link ProductResponseDTO} con los datos del producto creado
     * @throws IllegalArgumentException si alguna validación falla
     * 
     * @see ProductRequestDTO
     */
    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null)
            throw new IllegalArgumentException("ProductRequestDTO es null");

        if (productRequestDTO.getName() == null || productRequestDTO.getName().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (productRequestDTO.getPrice() == null || productRequestDTO.getPrice().signum() < 0)
            throw new IllegalArgumentException("Precio inválido");

        if (productRequestDTO.getStock() == null || productRequestDTO.getStock() < 0)
            throw new IllegalArgumentException("Cantidad inválida");

        Product prod = Product.builder()
                .name(productRequestDTO.getName())
                .category(productRequestDTO.getCategory())
                .price(productRequestDTO.getPrice())
                .stock(productRequestDTO.getStock())
                .active(productRequestDTO.getActive())
                .build();
        return Mapper.toDTO(repository.save(prod));
    }

    /**
     * Actualiza la información de un producto existente.
     * 
     * Solo actualiza los campos que son proporcionados en el DTO de solicitud.
     * Si un campo es nulo, no se modifica.
     * 
     * @param id                 identificador único del producto a actualizar
     * @param productRequestDTO objeto con los datos del producto a actualizar
     * @return {@link ProductResponseDTO} con los datos del producto actualizado
     * @throws NotFoundException si el producto no existe en la base de datos
     * 
     * @see ProductRequestDTO
     */
    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product no encontrado"));

        if (productRequestDTO.getName() != null)
            product.setName(productRequestDTO.getName());

        if (productRequestDTO.getCategory() != null)
            product.setCategory(productRequestDTO.getCategory());

        if (productRequestDTO.getStock() != null)
            product.setStock(productRequestDTO.getStock());

        if (productRequestDTO.getPrice() != null)
            product.setPrice(productRequestDTO.getPrice());

        if (productRequestDTO.getActive() != null)
            product.setActive(productRequestDTO.getActive());

        return Mapper.toDTO(repository.save(product));

    }

    /**
     * Elimina (desactiva) un producto específico.
     * 
     * Esta operación realiza una eliminación lógica: marca el producto como
     * inactivo
     * en lugar de borrarlo físicamente de la base de datos.
     * 
     * @param id identificador único del producto a eliminar
     * @throws NotFoundException si el producto no existe en la base de datos
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product no encontrado"));
        product.setActive(false);

        repository.save(product);

    }

}
