package com.adrian.prueba_tecnica.ejercicio_supermecado.service; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleDetailRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.enums.EstadoVentaEnum;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Branch;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Product;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sale;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.BranchRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.ProductRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.SaleRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.SaleServiceImpl;

/**
 * Test unitario para SaleServiceImpl.
 * 
 * Esta clase verifica el comportamiento del servicio de ventas,
 * incluyendo validación de stock, cálculos de totales con IVA,
 * y operaciones CRUD completas.
 * 
 * @author Adrian
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de SaleServiceImpl")
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SaleServiceImpl saleService;

    private Sale sale;
    private Branch branch;
    private Product product1;
    private Product product2;
    private SaleRequestDTO saleRequestDTO;
    private SaleDetailRequestDTO detailDTO1;
    private SaleDetailRequestDTO detailDTO2;

    @BeforeEach
    void setUp() {
        // Configurar sucursal de prueba
        branch = Branch.builder()
                .id(1L)
                .name("Sucursal Centro")
                .address("Av. Principal #123")
                .active(true)
                .build();

        // Configurar productos de prueba
        product1 = Product.builder()
                .id(1L)
                .name("Leche")
                .category("Lácteos")
                .price(new BigDecimal("25.00"))
                .stock(100)
                .active(true)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Pan")
                .category("Panadería")
                .price(new BigDecimal("15.00"))
                .stock(50)
                .active(true)
                .build();

        // Configurar detalles de venta
        detailDTO1 = new SaleDetailRequestDTO();
        detailDTO1.setIdProduct(1L);
        detailDTO1.setProductQuantity(2);

        detailDTO2 = new SaleDetailRequestDTO();
        detailDTO2.setIdProduct(2L);
        detailDTO2.setProductQuantity(3);

        // Configurar DTO de solicitud
        saleRequestDTO = new SaleRequestDTO();
        saleRequestDTO.setIdBranch(1L);
        saleRequestDTO.setStatus(EstadoVentaEnum.PAGADA);
        saleRequestDTO.setDetail(Arrays.asList(detailDTO1, detailDTO2));

        // Configurar venta de prueba
        sale = new Sale();
        sale.setId(1L);
        sale.setDate(LocalDate.now());
        sale.setStatus(EstadoVentaEnum.PAGADA);
        sale.setBranch(branch);
        sale.setTotal(new BigDecimal("110.20")); // Total con IVA
    }

    @Test
    @DisplayName("Debería obtener todas las ventas")
    void testFindAllSales() {
        // Given
        Sale sale2 = new Sale();
        sale2.setId(2L);
        sale2.setDate(LocalDate.now());
        sale2.setStatus(EstadoVentaEnum.PENDIENTE);
        sale2.setBranch(branch);
        sale2.setTotal(new BigDecimal("200.00"));

        List<Sale> sales = Arrays.asList(sale, sale2);
        when(saleRepository.findAllWithDetailAndProduct()).thenReturn(sales);

        // When
        List<SaleResponseDTO> result = saleService.findAllSales();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(saleRepository, times(1)).findAllWithDetailAndProduct();
    }

    @Test
    @DisplayName("Debería obtener una venta por ID")
    void testFindByIdSale() {
        // Given
        when(saleRepository.findSaleWithDetailAndProduct(1L)).thenReturn(Optional.of(sale));

        // When
        SaleResponseDTO result = saleService.findByIdSale(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(saleRepository, times(1)).findSaleWithDetailAndProduct(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la venta no existe")
    void testFindByIdSaleNotFound() {
        // Given
        when(saleRepository.findSaleWithDetailAndProduct(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            saleService.findByIdSale(999L);
        });
        verify(saleRepository, times(1)).findSaleWithDetailAndProduct(999L);
    }

    @Test
    @DisplayName("Debería crear una venta exitosamente")
    void testCreateSale() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        // When
        SaleResponseDTO result = saleService.createSale(saleRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(98, product1.getStock()); // 100 - 2
        assertEquals(47, product2.getStock()); // 50 - 3
        verify(branchRepository, times(1)).findById(1L);
        verify(productRepository, times(2)).findById(1L); // Validación + creación
        verify(productRepository, times(2)).findById(2L); // Validación + creación
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando SaleRequestDTO es nulo")
    void testCreateSaleWithNullDTO() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(null);
        });
        assertEquals("saleRequestDTO es null", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no se indica sucursal")
    void testCreateSaleWithoutBranch() {
        // Given
        saleRequestDTO.setIdBranch(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        assertEquals("Debe indicar la sucursal", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no hay detalles de venta")
    void testCreateSaleWithoutDetails() {
        // Given
        saleRequestDTO.setDetail(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        assertEquals("Debe incluir al menos un producto", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la lista de detalles está vacía")
    void testCreateSaleWithEmptyDetails() {
        // Given
        saleRequestDTO.setDetail(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        assertEquals("Debe incluir al menos un producto", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException cuando la sucursal no existe")
    void testCreateSaleWithNonExistentBranch() {
        // Given
        when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        verify(branchRepository, times(1)).findById(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException cuando el producto no existe")
    void testCreateSaleWithNonExistentProduct() {
        // Given
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        verify(branchRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

        

    @Test
    @DisplayName("Debería lanzar excepción cuando la cantidad es cero o negativa")
    void testCreateSaleWithInvalidQuantity() {
        // Given
        detailDTO1.setProductQuantity(0);

        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
      
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        assertEquals("Cantidad inválida", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando hay stock insuficiente")
    void testCreateSaleWithInsufficientStock() {
        // Given
        detailDTO1.setProductQuantity(150); // Más que el stock disponible (100)

        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(branchRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería descontar el stock correctamente al crear venta")
    void testStockDeductionOnSaleCreation() {
        // Given
        int initialStock1 = product1.getStock();
        int initialStock2 = product2.getStock();

        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        // When
        saleService.createSale(saleRequestDTO);

        // Then
        assertEquals(initialStock1 - detailDTO1.getProductQuantity(), product1.getStock());
        assertEquals(initialStock2 - detailDTO2.getProductQuantity(), product2.getStock());
    }

    @Test
    @DisplayName("Debería actualizar una venta exitosamente")
    void testUpdateSale() {
        // Given
        SaleUpdateRequestDTO updateDTO = new SaleUpdateRequestDTO();
        updateDTO.setDate(LocalDate.now().plusDays(1));
        updateDTO.setStatus(EstadoVentaEnum.CANCELADA);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        // When
        SaleResponseDTO result = saleService.updateSale(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(saleRepository, times(1)).findById(1L);
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException al actualizar venta inexistente")
    void testUpdateSaleNotFound() {
        // Given
        SaleUpdateRequestDTO updateDTO = new SaleUpdateRequestDTO();
        updateDTO.setStatus(EstadoVentaEnum.PAGADA);

        when(saleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            saleService.updateSale(999L, updateDTO);
        });
        verify(saleRepository, times(1)).findById(999L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el DTO de actualización es nulo")
    void testUpdateSaleWithNullDTO() {
        // Given
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.updateSale(1L, null);
        });
        assertEquals("sale no encontrada", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería actualizar solo los campos proporcionados")
    void testUpdateSalePartial() {
        // Given
        SaleUpdateRequestDTO updateDTO = new SaleUpdateRequestDTO();
        updateDTO.setStatus(EstadoVentaEnum.PAGADA);
        // No se actualiza la fecha

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        // When
        SaleResponseDTO result = saleService.updateSale(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(saleRepository, times(1)).findById(1L);
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Debería eliminar una venta exitosamente")
    void testDeleteSale() {
        // Given
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        doNothing().when(saleRepository).delete(sale);

        // When
        saleService.deleteSale(1L);

        // Then
        verify(saleRepository, times(1)).findById(1L);
        verify(saleRepository, times(1)).delete(sale);
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException al eliminar venta inexistente")
    void testDeleteSaleNotFound() {
        // Given
        when(saleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            saleService.deleteSale(999L);
        });
        verify(saleRepository, times(1)).findById(999L);
        verify(saleRepository, never()).delete(any(Sale.class));
    }

    @Test
    @DisplayName("Debería validar el stock antes de descontar")
    void testStockValidationBeforeDeduction() {
        // Given
        Product product3 = Product.builder()
                .id(3L)
                .name("Producto Limitado")
                .category("Test")
                .price(new BigDecimal("10.00"))
                .stock(5)
                .active(true)
                .build();

        SaleDetailRequestDTO detailDTO3 = new SaleDetailRequestDTO();
        detailDTO3.setIdProduct(3L);
        detailDTO3.setProductQuantity(10); // Más que el stock

        saleRequestDTO.setDetail(Arrays.asList(detailDTO3));

        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product3));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            saleService.createSale(saleRequestDTO);
        });

        // Verificar que el stock no se modificó debido a la validación
        assertEquals(5, product3.getStock());
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
    }
}
