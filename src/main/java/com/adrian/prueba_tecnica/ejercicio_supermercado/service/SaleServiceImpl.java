package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleDetailRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.SaleDetail;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Product;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Branch;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sale;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.ProductRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.BranchRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.SaleRepository;

/**
 * Implementación del servicio de gestión de ventas.
 * 
 * Esta clase proporciona la lógica de negocio compleja para operaciones relacionadas con ventas,
 * incluyendo búsqueda, creación, actualización y eliminación de ventas.
 * Implementa la interfaz {@link ISaleService}.
 * 
 * Características principales:
 * - Obtención de lista de ventas con sus detalles y productos asociados
 * - Búsqueda de venta específica por identificador
 * - Creación de nuevas ventas con validación completa de stock
 * - Cálculo automático de totales incluyendo IVA (16%)
 * - Desconteo automático de stock al crear ventas
 * - Actualización de estado y fecha de ventas
 * - Eliminación (física) de registros de ventas
 * 
 * Nota: Implementa una validación en dos pasos al crear ventas:
 * 1. Validación de disponibilidad de stock
 * 2. Creación de detalles y desconteo de inventario
 * 
 * @author Adrian
 * @version 1.0
 * @see ISaleService
 * @see Sale
 * @see SaleDetail
 */
@Service
public class SaleServiceImpl implements ISaleService {

    /**
     * Factor para calcular el IVA (16% = 1.16).
     * Se utiliza para aumentar el subtotal de cada detalle de venta.
     */
    private static final BigDecimal IVA_FACTOR = BigDecimal.valueOf(1.16);

    /**
     * Logger para registrar información de operaciones y errores.
     */
    private static final Logger log = LoggerFactory.getLogger(SaleServiceImpl.class);

    /**
     * Repositorio para acceder a la información de ventas en la base de datos.
     */
    private SaleRepository saleRepository;

    /**
     * Repositorio para acceder a la información de productos en la base de datos.
     */
    private ProductRepository productRepository;

    /**
     * Repositorio para acceder a la información de sucursales en la base de datos.
     */
    private BranchRepository branchRepository;

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param saleRepository repositorio para acceder a datos de ventas
     * @param productRepository repositorio para acceder a datos de productos
     * @param branchRepository repositorio para acceder a datos de sucursales
     */
    public SaleServiceImpl(SaleRepository saleRepository, ProductRepository productRepository,
            BranchRepository branchRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
    }

    /**
     * Obtiene la lista de todas las ventas del sistema.
     * 
     * Este método busca todas las ventas en la base de datos incluyendo sus detalles
     * y productos asociados, y las convierte a objetos DTO para su devolución.
     * Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link SaleResponseDTO} con información de todas las ventas
     */
    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> findAllSales() {
        // Segunda forma de hacer la conversión (la primera está en los demás servicios)
        List<Sale> sales = saleRepository.findAllWithDetailAndProduct();
        List<SaleResponseDTO> DTO = new ArrayList<>();

        SaleResponseDTO dto;
        for (Sale v : sales) {
            dto = Mapper.toDTO(v);
            DTO.add(dto);
        }

        return DTO;
    }

    /**
     * Obtiene una venta específica por su identificador.
     * 
     * @param id identificador único de la venta a buscar
     * @return {@link SaleResponseDTO} con la información completa de la venta
     * @throws java.util.NoSuchElementException si la venta no existe
     */
    @Override
    public SaleResponseDTO findByIdSale(Long id) {
        return saleRepository.findSaleWithDetailAndProduct(id).map(Mapper::toDTO).orElseThrow();

    }

    /**
     * Crea una nueva venta con su detalle de productos.
     * 
     * Este método realiza una validación exhaustiva y un procesamiento en dos pasos:
     * 
     * Paso 1: Validación previa
     * - Verifica que el DTO de solicitud no sea nulo
     * - Verifica que se indique una sucursal
     * - Verifica que se incluya al menos un producto
     * - Verifica stock suficiente para todos los productos
     * 
     * Paso 2: Creación y desconteo de stock
     * - Crea los detalles de venta
     * - Descuenta el stock de cada producto
     * - Calcula el total con IVA (1.16x)
     * 
     * Nota: El cálculo del total incluye el IVA en cada subtotal de producto.
     * 
     * @param saleRequestDTO objeto con los datos de la venta a crear
     * @return {@link SaleResponseDTO} con los datos de la venta creada
     * @throws IllegalArgumentException si faltan datos obligatorios
     * @throws NotFoundException si la sucursal o producto no existen
     * @throws IllegalStateException si hay stock insuficiente
     * 
     * @see SaleRequestDTO
     * @see SaleDetailRequestDTO
     */
    @Override
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO saleRequestDTO) {

        if (saleRequestDTO == null)
            throw new IllegalArgumentException("saleRequestDTO es null");

        if (saleRequestDTO.getIdBranch() == null)
            throw new IllegalArgumentException("Debe indicar la sucursal");

        if (saleRequestDTO.getDetail() == null || saleRequestDTO.getDetail().isEmpty())
            throw new IllegalArgumentException("Debe incluir al menos un producto");

        Branch branch = branchRepository.findById(saleRequestDTO.getIdBranch())
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));

        Sale sale = new Sale();
        sale.setDate(LocalDate.now());
        sale.setStatus(saleRequestDTO.getStatus());
        sale.setBranch(branch);

        BigDecimal total = BigDecimal.ZERO;
        List<SaleDetail> details = new ArrayList<>();

        // PRIMER PASO: VALIDAR STOCK de todos los productos
        for (SaleDetailRequestDTO dto : saleRequestDTO.getDetail()) {

            if (dto.getProductQuantity() == null || dto.getProductQuantity() <= 0)
                throw new IllegalArgumentException("Cantidad inválida");

            Product product = productRepository.findById(dto.getIdProduct())
                    .orElseThrow(() -> new NotFoundException("Product no encontrado"));

            if (dto.getProductQuantity() > product.getStock())
                throw new IllegalStateException(
                        "Stock insuficiente para el product: " + product.getName());
        }

        // SEGUNDO PASO: CREAR DETALLES y DESCONTAR STOCK
        for (SaleDetailRequestDTO dto : saleRequestDTO.getDetail()) {

            Product product = productRepository.findById(dto.getIdProduct())
                    .orElseThrow(() -> new NotFoundException("Product no encontrado"));

            // Descontamos del stock disponible
            product.setStock(product.getStock() - dto.getProductQuantity());

            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setPrice(product.getPrice());
            detail.setProductQuantity(dto.getProductQuantity());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(dto.getProductQuantity()));

            detail.setSubtotal(subtotal);
            detail.setSale(sale);

            details.add(detail);

            // Calculamos el subtotal con IVA incluido
            BigDecimal subtotalIva = subtotal.multiply(IVA_FACTOR);
            total = total.add(subtotalIva);
        }

        sale.setDetail(details);
        sale.setTotal(total);

        sale = saleRepository.save(sale);

        return Mapper.toDTO(sale);
    }

    /**
     * Actualiza los datos de una venta existente.
     * 
     * Permite actualizar la fecha y el estado de una venta.
     * Solo actualiza los campos que son proporcionados en el DTO.
     * 
     * @param id identificador único de la venta a actualizar
     * @param saleDTO objeto con los datos de la venta a actualizar
     * @return {@link SaleResponseDTO} con los datos de la venta actualizada
     * @throws NotFoundException si la venta no existe
     * @throws IllegalArgumentException si el DTO es nulo
     * 
     * @see SaleUpdateRequestDTO
     */
    @Override
    @Transactional
    public SaleResponseDTO updateSale(Long id, SaleUpdateRequestDTO saleDTO) {
        log.info("Actualizando venta id: {}, DTO: {}", id, saleDTO);
        // Buscar si la venta existe para actualizarla
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("sale no encontrada"));

        if (saleDTO == null)
            throw new IllegalArgumentException("sale no encontrada");
        if (saleDTO.getDate() != null) {
            sale.setDate(saleDTO.getDate());
        }
        if (saleDTO.getStatus() != null) {
            sale.setStatus(saleDTO.getStatus());
        }
        saleRepository.save(sale);
        SaleResponseDTO saleCreated = Mapper.toDTO(sale);
        return saleCreated;
    }

    /**
     * Elimina una venta del sistema.
     * 
     * Esta operación realiza una eliminación física (eliminación permanente)
     * del registro de venta.
     * 
     * Nota: No se restaura el stock de los productos vendidos.
     * 
     * @param id identificador único de la venta a eliminar
     * @throws NotFoundException si la venta no existe
     */
    @Override
    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new NotFoundException("Venta no encontrada"));
        saleRepository.delete(sale);
    }

}
