package com.adrian.prueba_tecnica.ejercicio_supermercado.mapper;

import java.util.stream.Collectors;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.producto.ProductResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.sucursal.BranchResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.RoleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.user.UserResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleDetailResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Product;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Branch;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.User;
import com.adrian.prueba_tecnica.ejercicio_supermercado.model.Sale;

/**
 * Clase utilitaria para la conversión de entidades a objetos DTO (Data Transfer
 * Objects).
 * 
 * Esta clase implementa el patrón Mapper para facilitar la transformación entre
 * entidades del modelo de dominio y sus correspondientes DTOs de respuesta.
 * Utiliza métodos estáticos de sobrecarga para manejar conversiones de
 * diferentes tipos de entidades.
 * 
 * Mapeos soportados:
 * 
 * {@link Product} → {@link ProductResponseDTO}
 * {@link Sale} → {@link SaleResponseDTO} (incluyendo detalles anidados)
 * {@link Branch} → {@link BranchResponseDTO}
 * {@link User} → {@link UserResponseDTO} (incluyendo roles anidados)
 * 
 * 
 * Características:
 * 
 * Validación nula: retorna null si la entidad de entrada es null
 * Conversión anidada: convierte automáticamente objetos relacionados
 * Conversión de colecciones: mapea listas de objetos relacionados usando
 * Streams
 * Constructor builder: utiliza patrones Builder para construcción de DTOs
 * 
 * 
 * @author Adrian
 * @version 1.0
 * @see ProductResponseDTO
 * @see SaleResponseDTO
 * @see BranchResponseDTO
 * @see UserResponseDTO
 */
public class Mapper {

    /**
     * Convierte una entidad Producto a su correspondiente DTO de respuesta.
     * 
     * Mapea todos los campos de la entidad Producto incluyendo:
     * 
     * Identificador único
     * Nombre y categoría
     * Precio unitario y cantidad en inventario
     * Estado activo/inactivo
     * Fechas de creación y actualización
     * 
     * 
     * @param p entidad {@link Product} a convertir
     * @return {@link ProductResponseDTO} con los datos del producto, o null si la
     *         entrada es null
     * 
     * @see Product
     * @see ProductResponseDTO
     */
    // Mapeo de Producto a ProductoDTO
    public static ProductResponseDTO toDTO(Product p) {
        if (p == null)
            return null;
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .price(p.getPrice())
                .stock(p.getStock())
                .active(p.isActive())
                .creationDate(p.getCreationDate())
                .updateCreation(p.getUpdateDate())
                .build();
    }

    /**
     * Convierte una entidad Venta a su correspondiente DTO de respuesta.
     * 
     * Realiza una conversión compleja que incluye:
     * 
     * Mapeo de datos básicos de la venta (ID, fecha, estado, total)
     * Conversión anidada de la sucursal asociada
     * Conversión en cascada de todos los DetalleVenta (líneas de venta):
     * 
     * Para cada detalle: crea DetalleVentaResponseDTO con información del producto
     * Incluye nombre del producto, cantidad, precio unitario, subtotal
     * Mantiene referencia al ID del producto original
     * 
     * Incluye fechas de creación y actualización
     * 
     * 
     * Esta operación es crítica para la serialización de ventas en respuestas API,
     * ya que proporciona una vista completa de la transacción con todos sus
     * detalles.
     * 
     * @param venta entidad {@link Sale} a convertir con sus detalles cargados
     * @return {@link SaleResponseDTO} con todos los datos de la venta incluyendo
     *         detalles anidados,
     *         o null si la entrada es null
     * 
     * @see Sale
     * @see SaleResponseDTO
     * @see SaleDetailResponseDTO
     */
    // Mapeo de Venta a VentaDTO
    public static SaleResponseDTO toDTO(Sale venta) {
        if (venta == null)
            return null;
        // Obtenemos la lista de DetalleVenta y convertimos a lista de DetalleVentaDTO
        var detail = venta.getDetail().stream().map(det -> SaleDetailResponseDTO.builder()
                .id(det.getId())
                .ProductName(det.getProduct().getName())
                .ProductQuantity(det.getProductQuantity())
                .price(det.getPrice())
                .subtotal(det.getSubtotal())
                .idProduct(det.getProduct().getId())
                .build()).collect(Collectors.toList());

        // Construimos VentaDTO con los datos recolectados
        return SaleResponseDTO.builder()
                .id(venta.getId())
                .date(venta.getDate())
                .idBranch(venta.getBranch().getId())
                .status(venta.getStatus())
                .detail(detail)
                .total(venta.getTotal())
                .creationDate(venta.getCreationDate())
                .updateDate(venta.getUpdateDate())
                .build();
    }

    /**
     * Convierte una entidad Sucursal a su correspondiente DTO de respuesta.
     * 
     * Mapea todos los campos de la entidad Sucursal incluyendo:
     * 
     * Identificador único
     * Nombre y dirección física
     * Estado activo/inactivo
     * Fechas de creación y actualización
     * 
     * 
     * @param s entidad {@link Branch} a convertir
     * @return {@link BranchResponseDTO} con los datos de la sucursal, o null si
     *         la entrada es null
     * 
     * @see Branch
     * @see BranchResponseDTO
     */
    // Mapeo de Sucursal a SucursalDTO
    public static BranchResponseDTO toDTO(Branch s) {
        if (s == null)
            return null;
        return BranchResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .address(s.getAddress())
                .active(s.isActive())
                .creationDate(s.getCreationDate())
                .updateDate(s.getUpdateDate())
                .build();
    }

    /**
     * Convierte una entidad User a su correspondiente DTO de respuesta.
     * 
     * Realiza una conversión que incluye:
     * 
     * Mapeo de datos básicos del usuario (ID, username, estado enabled)
     * Conversión en cascada de roles asociados:
     * 
     * Para cada Role: crea RoleResponseDTO con ID y nombre
     * Colecta todos los roles en una lista ordenada
     * 
     * Inclusión de la bandera admin (indicador de privilegios de administrador)
     * Inclusión de fechas de creación y actualización
     * 
     * 
     * Esta operación es importante para proporcionar información completa del
     * usuario
     * en respuestas API, incluyendo sus asignaciones de roles para evaluación de
     * permisos.
     * 
     * @param user entidad {@link User} a convertir con sus roles cargados
     * @return {@link UserResponseDTO} con todos los datos del usuario incluyendo
     *         roles anidados,
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
                .creationDate(user.getCreationDate())
                .UpdateDate(user.getUpdateDate())
                .build();
    }
}
