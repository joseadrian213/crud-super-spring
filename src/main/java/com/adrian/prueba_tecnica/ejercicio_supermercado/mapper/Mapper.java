package com.adrian.prueba_tecnica.ejercicio_supermercado.mapper;

import java.util.stream.Collectors;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductoResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.SucursalResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.RoleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.UserResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.DetalleVentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Producto;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sucursal;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Venta;

/**
 * Clase utilitaria para la conversión de entidades a objetos DTO (Data Transfer Objects).
 * 
 * Esta clase implementa el patrón Mapper para facilitar la transformación entre
 * entidades del modelo de dominio y sus correspondientes DTOs de respuesta.
 * Utiliza métodos estáticos de sobrecarga para manejar conversiones de diferentes tipos de entidades.
 * 
 * <p><strong>Mapeos soportados:</strong></p>
 * <ul>
 *   <li>{@link Producto} → {@link ProductoResponseDTO}
 *   <li>{@link Venta} → {@link VentaResponseDTO} (incluyendo detalles anidados)
 *   <li>{@link Sucursal} → {@link SucursalResponseDTO}
 *   <li>{@link User} → {@link UserResponseDTO} (incluyendo roles anidados)
 * </ul>
 * 
 * <p><strong>Características:</strong></p>
 * <ul>
 *   <li>Validación nula: retorna null si la entidad de entrada es null
 *   <li>Conversión anidada: convierte automáticamente objetos relacionados
 *   <li>Conversión de colecciones: mapea listas de objetos relacionados usando Streams
 *   <li>Constructor builder: utiliza patrones Builder para construcción de DTOs
 * </ul>
 * 
 * @author Adrian
 * @version 1.0
 * @see ProductoResponseDTO
 * @see VentaResponseDTO
 * @see SucursalResponseDTO
 * @see UserResponseDTO
 */
public class Mapper {
    
    /**
     * Convierte una entidad Producto a su correspondiente DTO de respuesta.
     * 
     * Mapea todos los campos de la entidad Producto incluyendo:
     * <ul>
     *   <li>Identificador único
     *   <li>Nombre y categoría
     *   <li>Precio unitario y cantidad en inventario
     *   <li>Estado activo/inactivo
     *   <li>Fechas de creación y actualización
     * </ul>
     * 
     * @param p entidad {@link Producto} a convertir
     * @return {@link ProductoResponseDTO} con los datos del producto, o null si la entrada es null
     * 
     * @see Producto
     * @see ProductoResponseDTO
     */
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
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte una entidad Venta a su correspondiente DTO de respuesta.
     * 
     * Realiza una conversión compleja que incluye:
     * <ol>
     *   <li>Mapeo de datos básicos de la venta (ID, fecha, estado, total)
     *   <li>Conversión anidada de la sucursal asociada
     *   <li>Conversión en cascada de todos los DetalleVenta (líneas de venta):
     *       <ul>
     *         <li>Para cada detalle: crea DetalleVentaResponseDTO con información del producto
     *         <li>Incluye nombre del producto, cantidad, precio unitario, subtotal
     *         <li>Mantiene referencia al ID del producto original
     *       </ul>
     *   <li>Incluye fechas de creación y actualización
     * </ol>
     * 
     * Esta operación es crítica para la serialización de ventas en respuestas API,
     * ya que proporciona una vista completa de la transacción con todos sus detalles.
     * 
     * @param venta entidad {@link Venta} a convertir con sus detalles cargados
     * @return {@link VentaResponseDTO} con todos los datos de la venta incluyendo detalles anidados,
     *         o null si la entrada es null
     * 
     * @see Venta
     * @see VentaResponseDTO
     * @see DetalleVentaResponseDTO
     */
    // Mapeo de Venta a VentaDTO
    public static VentaResponseDTO toDTO(Venta venta) {
        if (venta == null)
            return null;
        // Obtenemos la lista de DetalleVenta y convertimos a lista de DetalleVentaDTO
        var detalle = venta.getDetalle().stream().map(det -> DetalleVentaResponseDTO.builder()
                .id(det.getId())
                .nombreProd(det.getProducto().getNombre())
                .cantProd(det.getCantProd())
                .precio(det.getPrecio())
                .subtotal(det.getSubtotal())
                .idProducto(det.getProducto().getId())
                .build()).collect(Collectors.toList());

        // Construimos VentaDTO con los datos recolectados
        return VentaResponseDTO.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .idSucursal(venta.getSucursal().getId())
                .estado(venta.getEstado())
                .detalle(detalle)
                .total(venta.getTotal())
                .fechaCreacion(venta.getFechaCreacion())
                .fechaActualizacion(venta.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte una entidad Sucursal a su correspondiente DTO de respuesta.
     * 
     * Mapea todos los campos de la entidad Sucursal incluyendo:
     * <ul>
     *   <li>Identificador único
     *   <li>Nombre y dirección física
     *   <li>Estado activo/inactivo
     *   <li>Fechas de creación y actualización
     * </ul>
     * 
     * @param s entidad {@link Sucursal} a convertir
     * @return {@link SucursalResponseDTO} con los datos de la sucursal, o null si la entrada es null
     * 
     * @see Sucursal
     * @see SucursalResponseDTO
     */
    // Mapeo de Sucursal a SucursalDTO
    public static SucursalResponseDTO toDTO(Sucursal s) {
        if (s == null)
            return null;
        return SucursalResponseDTO.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .activo(s.isActivo())
                .fechaCreacion(s.getFechaCreacion())
                .fechaActualizacion(s.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte una entidad User a su correspondiente DTO de respuesta.
     * 
     * Realiza una conversión que incluye:
     * <ol>
     *   <li>Mapeo de datos básicos del usuario (ID, username, estado enabled)
     *   <li>Conversión en cascada de roles asociados:
     *       <ul>
     *         <li>Para cada Role: crea RoleResponseDTO con ID y nombre
     *         <li>Colecta todos los roles en una lista ordenada
     *       </ul>
     *   <li>Inclusión de la bandera admin (indicador de privilegios de administrador)
     *   <li>Inclusión de fechas de creación y actualización
     * </ol>
     * 
     * Esta operación es importante para proporcionar información completa del usuario
     * en respuestas API, incluyendo sus asignaciones de roles para evaluación de permisos.
     * 
     * @param user entidad {@link User} a convertir con sus roles cargados
     * @return {@link UserResponseDTO} con todos los datos del usuario incluyendo roles anidados,
     *         o null si la entrada es null
     * 
     * @see User
     * @see UserResponseDTO
     * @see RoleResponseDTO
     */
    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        var role = user.getRoles().stream()
                .map(rol -> RoleResponseDTO.builder()
                        .id(rol.getId())
                        .name(rol.getName())
                        .build())
                .collect(Collectors.toList());
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .admin(user.getAdmin())
                .roles(role)
                .enabled(user.getEnabled())
                .fechaCreacion(user.getFechaCreacion())
                .fechaActualizacion(user.getFechaActualizacion())
                .build();
    }
}
