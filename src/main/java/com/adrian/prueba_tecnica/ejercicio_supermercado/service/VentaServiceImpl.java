package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.DetalleVentaRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.DetalleVenta;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Producto;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sucursal;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Venta;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.ProductoRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.SucursalRepository;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.VentaRepository;

/**
 * Implementación del servicio de gestión de ventas.
 * 
 * Esta clase proporciona la lógica de negocio compleja para operaciones relacionadas con ventas,
 * incluyendo búsqueda, creación, actualización y eliminación de ventas.
 * Implementa la interfaz {@link IVentasService}.
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
 * @see IVentasService
 * @see Venta
 * @see DetalleVenta
 */
@Service
public class VentaServiceImpl implements IVentasService {

    /**
     * Factor para calcular el IVA (16% = 1.16).
     * Se utiliza para aumentar el subtotal de cada detalle de venta.
     */
    private static final BigDecimal IVA_FACTOR = BigDecimal.valueOf(1.16);

    /**
     * Logger para registrar información de operaciones y errores.
     */
    private static final Logger log = LoggerFactory.getLogger(VentaServiceImpl.class);

    /**
     * Repositorio para acceder a la información de ventas en la base de datos.
     */
    private VentaRepository ventaRepository;

    /**
     * Repositorio para acceder a la información de productos en la base de datos.
     */
    private ProductoRepository productoRepository;

    /**
     * Repositorio para acceder a la información de sucursales en la base de datos.
     */
    private SucursalRepository sucursalRepository;

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param ventaRepository repositorio para acceder a datos de ventas
     * @param productoRepository repositorio para acceder a datos de productos
     * @param sucursalRepository repositorio para acceder a datos de sucursales
     */
    public VentaServiceImpl(VentaRepository ventaRepository, ProductoRepository productoRepository,
            SucursalRepository sucursalRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Obtiene la lista de todas las ventas del sistema.
     * 
     * Este método busca todas las ventas en la base de datos incluyendo sus detalles
     * y productos asociados, y las convierte a objetos DTO para su devolución.
     * Solo realiza lectura (readOnly).
     * 
     * @return lista de {@link VentaResponseDTO} con información de todas las ventas
     */
    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> traerVentas() {
        // Segunda forma de hacer la conversión (la primera está en los demás servicios)
        List<Venta> ventas = ventaRepository.findAllConDetalleYProducto();
        List<VentaResponseDTO> ventaDTO = new ArrayList<>();

        VentaResponseDTO dto;
        for (Venta v : ventas) {
            dto = Mapper.toDTO(v);
            ventaDTO.add(dto);
        }

        return ventaDTO;
    }

    /**
     * Obtiene una venta específica por su identificador.
     * 
     * @param id identificador único de la venta a buscar
     * @return {@link VentaResponseDTO} con la información completa de la venta
     * @throws java.util.NoSuchElementException si la venta no existe
     */
    @Override
    public VentaResponseDTO getVentaDTO(Long id) {
        return ventaRepository.findVentaConDetalleYProducto(id).map(Mapper::toDTO).orElseThrow();

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
     * @param ventaRequestDTO objeto con los datos de la venta a crear
     * @return {@link VentaResponseDTO} con los datos de la venta creada
     * @throws IllegalArgumentException si faltan datos obligatorios
     * @throws NotFoundException si la sucursal o producto no existen
     * @throws IllegalStateException si hay stock insuficiente
     * 
     * @see VentaRequestDTO
     * @see DetalleVentaRequestDTO
     */
    @Override
    @Transactional
    public VentaResponseDTO crearVenta(VentaRequestDTO ventaRequestDTO) {

        if (ventaRequestDTO == null)
            throw new IllegalArgumentException("ventaRequestDTO es null");

        if (ventaRequestDTO.getIdSucursal() == null)
            throw new IllegalArgumentException("Debe indicar la sucursal");

        if (ventaRequestDTO.getDetalle() == null || ventaRequestDTO.getDetalle().isEmpty())
            throw new IllegalArgumentException("Debe incluir al menos un producto");

        Sucursal sucursal = sucursalRepository.findById(ventaRequestDTO.getIdSucursal())
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setEstado(ventaRequestDTO.getEstado());
        venta.setSucursal(sucursal);

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        // PRIMER PASO: VALIDAR STOCK de todos los productos
        for (DetalleVentaRequestDTO dto : ventaRequestDTO.getDetalle()) {

            if (dto.getCantProd() == null || dto.getCantProd() <= 0)
                throw new IllegalArgumentException("Cantidad inválida");

            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

            if (dto.getCantProd() > producto.getCantidad())
                throw new IllegalStateException(
                        "Stock insuficiente para el producto: " + producto.getNombre());
        }

        // SEGUNDO PASO: CREAR DETALLES y DESCONTAR STOCK
        for (DetalleVentaRequestDTO dto : ventaRequestDTO.getDetalle()) {

            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

            // Descontamos del stock disponible
            producto.setCantidad(producto.getCantidad() - dto.getCantProd());

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());
            detalle.setCantProd(dto.getCantProd());

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(dto.getCantProd()));

            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);

            detalles.add(detalle);

            // Calculamos el subtotal con IVA incluido
            BigDecimal subtotalConIva = subtotal.multiply(IVA_FACTOR);
            total = total.add(subtotalConIva);
        }

        venta.setDetalle(detalles);
        venta.setTotal(total);

        venta = ventaRepository.save(venta);

        return Mapper.toDTO(venta);
    }

    /**
     * Actualiza los datos de una venta existente.
     * 
     * Permite actualizar la fecha y el estado de una venta.
     * Solo actualiza los campos que son proporcionados en el DTO.
     * 
     * @param id identificador único de la venta a actualizar
     * @param ventaDTO objeto con los datos de la venta a actualizar
     * @return {@link VentaResponseDTO} con los datos de la venta actualizada
     * @throws NotFoundException si la venta no existe
     * @throws IllegalArgumentException si el DTO es nulo
     * 
     * @see VentaUpdateRequestDTO
     */
    @Override
    @Transactional
    public VentaResponseDTO actualizarVenta(Long id, VentaUpdateRequestDTO ventaDTO) {
        log.info("Actualizando venta id: {}, DTO: {}", id, ventaDTO);
        // Buscar si la venta existe para actualizarla
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada"));

        if (ventaDTO == null)
            throw new IllegalArgumentException("Venta no encontrada");
        if (ventaDTO.getFecha() != null) {
            venta.setFecha(ventaDTO.getFecha());
        }
        if (ventaDTO.getEstado() != null) {
            venta.setEstado(ventaDTO.getEstado());
        }
        ventaRepository.save(venta);
        VentaResponseDTO ventaSalida = Mapper.toDTO(venta);
        return ventaSalida;
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
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new NotFoundException("Venta no encontrada"));
        ventaRepository.delete(venta);
    }

}
