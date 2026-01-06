package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.math.BigDecimal;
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

@Service
public class VentaServiceImpl implements IVentasService {

    private static final BigDecimal IVA_FACTOR = BigDecimal.valueOf(1.16);

    private static final Logger log = LoggerFactory.getLogger(VentaServiceImpl.class);

    private VentaRepository ventaRepository;

    private ProductoRepository productoRepository;

    private SucursalRepository sucursalRepository;

    // @Autowired Dado por implicito
    public VentaServiceImpl(VentaRepository ventaRepository, ProductoRepository productoRepository,
            SucursalRepository sucursalRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> traerVentas() {
        // Segunda forma de hacer la conversion la primera esta en los demas service
        List<Venta> ventas = ventaRepository.findAllConDetalleYProducto();
        List<VentaResponseDTO> ventaDTO = new ArrayList<>();

        VentaResponseDTO dto;
        for (Venta v : ventas) {
            dto = Mapper.toDTO(v);
            ventaDTO.add(dto);
        }

        return ventaDTO;
    }

    @Override
    public VentaResponseDTO getVentaDTO(Long id) {
        return ventaRepository.findVentaConDetalleYProducto(id).map(Mapper::toDTO).orElseThrow();

    }

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
        venta.setFecha(ventaRequestDTO.getFecha());
        venta.setEstado(ventaRequestDTO.getEstado());
        venta.setSucursal(sucursal);

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        // PRIMER PASO: VALIDAR STOCK
        for (DetalleVentaRequestDTO dto : ventaRequestDTO.getDetalle()) {

            if (dto.getCantProd() == null || dto.getCantProd() <= 0)
                throw new IllegalArgumentException("Cantidad inválida");

            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

            if (dto.getCantProd() > producto.getCantidad())
                throw new IllegalStateException(
                        "Stock insuficiente para el producto: " + producto.getNombre());
        }

        // SEGUNDO PASO: CREAR DETALLES Y DESCONTAR STOCK
        for (DetalleVentaRequestDTO dto : ventaRequestDTO.getDetalle()) {

            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

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

            BigDecimal subtotalConIva = subtotal.multiply(IVA_FACTOR);
            total = total.add(subtotalConIva);
        }

        venta.setDetalle(detalles);
        venta.setTotal(total);

        venta = ventaRepository.save(venta);

        return Mapper.toDTO(venta);
    }

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

    @Override
    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new NotFoundException("Venta no encontrada"));
        ventaRepository.delete(venta);
    }

}
