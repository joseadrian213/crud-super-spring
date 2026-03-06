package com.adrian.prueba_tecnica.ejercicio_supermecado.service; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.producto.ProductRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Product;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.ProductRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.ProductServiceImpl;

/**
 * Test unitario para ProductServiceImpl.
 * 
 * Esta clase verifica el comportamiento del servicio de productos,
 * incluyendo operaciones CRUD y validaciones de negocio.
 * 
 * @author Adrian
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        product = Product.builder()
                .id(1L)
                .name("Leche Deslactosada")
                .category("Lácteos")
                .price(new BigDecimal("25.50"))
                .stock(100)
                .active(true)
                .build();

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Leche Deslactosada");
        productRequestDTO.setCategory("Lácteos");
        productRequestDTO.setPrice(new BigDecimal("25.50"));
        productRequestDTO.setStock(100);
        productRequestDTO.setActive(true);
    }

    @Test
    @DisplayName("Debería obtener todos los productos")
    void testFindAllProducts() {
        // Given
        Product product2 = Product.builder()
                .id(2L)
                .name("Pan Integral")
                .category("Panadería")
                .price(new BigDecimal("15.00"))
                .stock(50)
                .active(true)
                .build();

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<ProductResponseDTO> result = productService.findAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Leche Deslactosada", result.get(0).getName());
        assertEquals("Pan Integral", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un producto por ID")
    void testFindByIdProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        ProductResponseDTO result = productService.findByIdProduct(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Leche Deslactosada", result.getName());
        assertEquals("Lácteos", result.getCategory());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException cuando el producto no existe")
    void testFindByIdProductNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            productService.findByIdProduct(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería crear un producto exitosamente")
    void testCreateProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Leche Deslactosada", result.getName());
        assertEquals("Lácteos", result.getCategory());
        assertEquals(new BigDecimal("25.50"), result.getPrice());
        assertEquals(100, result.getStock());
        assertTrue(result.isActive());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el ProductRequestDTO es nulo")
    void testCreateProductWithNullDTO() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(null);
        });
        assertEquals("ProductRequestDTO es null", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el nombre es nulo")
    void testCreateProductWithNullName() {
        // Given
        productRequestDTO.setName(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequestDTO);
        });
        assertEquals("El nombre es obligatorio", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el nombre está vacío")
    void testCreateProductWithBlankName() {
        // Given
        productRequestDTO.setName("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequestDTO);
        });
        assertEquals("El nombre es obligatorio", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el precio es nulo")
    void testCreateProductWithNullPrice() {
        // Given
        productRequestDTO.setPrice(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequestDTO);
        });
        assertEquals("Precio inválido", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el precio es negativo")
    void testCreateProductWithNegativePrice() {
        // Given
        productRequestDTO.setPrice(new BigDecimal("-10.00"));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequestDTO);
        });
        assertEquals("Precio inválido", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el stock es nulo")
    void testCreateProductWithNullStock() {
        // Given
        productRequestDTO.setStock(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequestDTO);
        });
        assertEquals("Cantidad inválida", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el stock es negativo")
    void testCreateProductWithNegativeStock() {
        // Given
        productRequestDTO.setStock(-5);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequestDTO);
        });
        assertEquals("Cantidad inválida", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería actualizar un producto exitosamente")
    void testUpdateProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Leche Light");
        updateDTO.setPrice(new BigDecimal("30.00"));

        // When
        ProductResponseDTO result = productService.updateProduct(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería actualizar solo los campos proporcionados")
    void testUpdateProductPartial() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Leche Light");
        // No se actualizan otros campos

        // When
        ProductResponseDTO result = productService.updateProduct(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException al actualizar producto inexistente")
    void testUpdateProductNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Leche Light");

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(999L, updateDTO);
        });
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería eliminar (desactivar) un producto exitosamente")
    void testDeleteProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        productService.deleteProduct(1L);

        // Then
        assertFalse(product.isActive());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException al eliminar producto inexistente")
    void testDeleteProductNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
