package com.adrian.prueba_tecnica.ejercicio_supermercado.mapper;

import java.util.stream.Collectors;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductoResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.SucursalResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.DetalleVentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Producto;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sucursal;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Venta;

public class Mapper {
    // Mapeo de Producto a ProductoDTO
    public static ProductoResponseDTO toDTO(Producto p) {
        if (p == null)
            return null;
        return ProductoResponseDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .categoria(p.getCategoria())
                .precio(p.getPrecio())
                .cantidad(p.getCantidad())
                .activo(p.isActivo())
                .build();
    }

    // Mapeo de Venta a VentaDTO
    public static VentaResponseDTO toDTO(Venta venta) {
        if (venta == null)
            return null;
        // Obtenemos la lista de DetalleVentay convertimos a lista de DetalleVentaDTO
        var detalle = venta.getDetalle().stream().map(det -> DetalleVentaResponseDTO.builder()
                .id(det.getId())
                .nombreProd(det.getProducto().getNombre())
                .cantProd(det.getCantProd())
                .precio(det.getPrecio())
                .subtotal(det.getSubtotal()) 
                .idProducto(det.getProducto().getId())
                .build()).collect(Collectors.toList());

                
        //Contruimo VentaDTO con los datos recolectados
        return VentaResponseDTO.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .idSucursal(venta.getSucursal().getId())
                .estado(venta.getEstado())
                .detalle(detalle)
                .total(venta.getTotal())
                .build();
    }

    // Mapeo de Sucursal a SucursalDTO
    public static SucursalResponseDTO toDTO(Sucursal s) {
        if (s == null)
            return null;
        return SucursalResponseDTO.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .activo(s.isActivo())
                .build();
    }
}
