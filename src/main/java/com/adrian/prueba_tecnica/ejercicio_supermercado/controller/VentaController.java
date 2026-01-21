package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.VentaUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IVentasService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST para la gestión de ventas (transacciones de compra).
 * 
 * Este controlador proporciona endpoints para realizar operaciones CRUD completas
 * sobre ventas, incluyendo:
 * <ul>
 *   <li>Listar todas las ventas con sus detalles
 *   <li>Obtener una venta específica por ID
 *   <li>Crear nuevas ventas (con validación de stock en dos fases)
 *   <li>Actualizar ventas existentes (fecha, estado, sucursal)
 *   <li>Eliminar ventas (eliminación física)
 * </ul>
 * 
 * <p><strong>Rutas de acceso:</strong></p>
 * <ul>
 *   <li>GET /api/ventas - Obtiene la lista de todas las ventas
 *   <li>GET /api/ventas/{id} - Obtiene una venta específica
 *   <li>POST /api/ventas - Crea una nueva venta
 *   <li>PUT /api/ventas/{id} - Actualiza una venta existente
 *   <li>DELETE /api/ventas/{id} - Elimina una venta
 * </ul>
 * 
 * <p><strong>Control de acceso:</strong></p>
 * Los endpoints de GET, POST y PUT requieren autenticación con rol ADMIN o USER.
 * El endpoint DELETE está disponible sin restricción de roles específica.
 * 
 * <p><strong>Procesamiento de ventas:</strong></p>
 * La creación de ventas implementa un proceso de validación en dos fases:
 * <ol>
 *   <li>FASE 1 - VALIDACIÓN: Verifica que todos los productos solicitados tengan stock suficiente
 *   <li>FASE 2 - CREACIÓN: Crea los detalles de venta, decrementa cantidades, calcula totales con IVA
 * </ol>
 * El IVA se aplica al 16% (factor 1.16) sobre cada detalle de venta.
 * 
 * @author Adrian
 * @version 1.0
 * @see IVentasService
 * @see VentaResponseDTO
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    /**
     * Servicio para la gestión de lógica de negocio de ventas.
     * Se utiliza para operaciones CRUD y procesamiento de transacciones.
     */
    private IVentasService ventasService;

    /**
     * Constructor que inyecta el servicio de ventas.
     * 
     * @param ventasService servicio para gestionar la lógica de negocio de ventas
     */
    public VentaController(IVentasService ventasService) {
        this.ventasService = ventasService;
    }

    /**
     * Obtiene la lista de todas las ventas registradas en el sistema.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Retorna una lista completa de objetos {@link VentaResponseDTO} con información
     * de todas las ventas incluyendo sus detalles (productos, cantidades, precios).
     * Se realiza como consulta de solo lectura optimizando el acceso a la base de datos.
     * 
     * @return {@link ResponseEntity} con la lista de {@link VentaResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<VentaResponseDTO>> getVentas() {
        return ResponseEntity.ok(ventasService.traerVentas());
    }

    /**
     * Obtiene una venta específica por su identificador.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Busca y retorna los detalles completos de la venta incluyendo todos los productos
     * que forman parte de la transacción, sus cantidades, precios unitarios y subtotales.
     * 
     * @param id identificador único de la venta a obtener
     * @return {@link ResponseEntity} con el {@link VentaResponseDTO} correspondiente
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la venta con el ID especificado no existe
     * 
     * @see VentaResponseDTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<VentaResponseDTO> getVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.getVentaDTO(id));
    }

    /**
     * Crea una nueva venta con los productos solicitados.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Implementa un proceso de validación en dos fases:
     * <ol>
     *   <li><strong>FASE 1 (VALIDACIÓN):</strong> Verifica que todos los productos tengan stock suficiente.
     *        Si algún producto no tiene suficiente cantidad, lanza excepción y no se crea la venta.
     *   <li><strong>FASE 2 (CREACIÓN):</strong> Si la validación pasa, crea los detalles de venta,
     *        decrementa las cantidades en inventario, calcula subtotales y aplica IVA (factor 1.16).
     * </ol>
     * 
     * El total de la venta se calcula sumando todos los subtotales de detalles con IVA aplicado.
     * 
     * Si la creación es exitosa, retorna la ubicación del nuevo recurso
     * en el encabezado "Location".
     * 
     * @param ventaDTO objeto {@link VentaRequestDTO} con los datos de la venta a crear
     *                 (sucursalId, lista de productos con cantidades)
     * @return {@link ResponseEntity} con el {@link VentaResponseDTO} creado
     *         con estado HTTP 201 (Created) y encabezado Location
     * @throws IllegalArgumentException si los datos de la venta son inválidos
     * @throws NotFoundException si la sucursal o algún producto no existen
     * @throws RuntimeException si algún producto no tiene stock suficiente
     * 
     * @see VentaRequestDTO
     * @see VentaResponseDTO
     * @see IVentasService#crearVenta(VentaRequestDTO)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> create(@Valid @RequestBody VentaRequestDTO ventaDTO) {
        VentaResponseDTO created = ventasService.crearVenta(ventaDTO);
        return ResponseEntity.created(URI.create("/api/ventas/" + created.getId())).body(created);
    }

    /**
     * Actualiza una venta existente.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Permite actualizar los siguientes campos de una venta:
     * <ul>
     *   <li>Fecha de la venta
     *   <li>Estado de la venta (PENDIENTE, PAGADA, CANCELADA)
     *   <li>Sucursal asociada
     *   <li>Total de la venta
     * </ul>
     * 
     * Solo actualiza los campos proporcionados en el DTO. Los campos no especificados
     * (null) no se modifican, permitiendo actualizaciones parciales.
     * 
     * <strong>Nota:</strong> Los detalles de la venta (productos) no se pueden actualizar
     * directamente a través de este endpoint. Para cambiar los productos, debe eliminar
     * y crear una nueva venta.
     * 
     * @param id identificador único de la venta a actualizar
     * @param ventaDTO objeto {@link VentaUpdateRequestDTO} con los datos a actualizar
     * @return {@link ResponseEntity} con el {@link VentaResponseDTO} actualizado
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la venta no existe
     * 
     * @see VentaUpdateRequestDTO
     * @see VentaResponseDTO
     * @see IVentasService#actualizarVenta(Long, VentaUpdateRequestDTO)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody VentaUpdateRequestDTO ventaDTO) {
        return ResponseEntity.ok(ventasService.actualizarVenta(id, ventaDTO));
    }

    /**
     * Elimina una venta específica del sistema.
     * 
     * Esta operación realiza una eliminación física: elimina completamente la venta
     * de la base de datos, incluyendo todos sus detalles de venta.
     * 
     * <strong>Advertencia:</strong> La eliminación de una venta NO restaura el inventario
     * de los productos. Las cantidades decrementadas durante la creación de la venta
     * no serán devueltas al stock de productos.
     * 
     * @param id identificador único de la venta a eliminar
     * @return {@link ResponseEntity} sin contenido con estado HTTP 204 (No Content)
     * @throws NotFoundException si la venta no existe
     * 
     * @see IVentasService#eliminarVenta(Long)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventasService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }

}
