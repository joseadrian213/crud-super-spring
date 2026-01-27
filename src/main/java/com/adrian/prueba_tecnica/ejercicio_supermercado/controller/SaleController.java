package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.request.venta.SaleUpdateRequestDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.service.ISaleService;

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
 * 
 *   Listar todas las ventas con sus detalles
 *   Obtener una venta específica por ID
 *   Crear nuevas ventas (con validación de stock en dos fases)
 *   Actualizar ventas existentes (fecha, estado, sucursal)
 *   Eliminar ventas (eliminación física)
 * 
 * 
 * Rutas de acceso:
 * 
 *   GET /api/ventas - Obtiene la lista de todas las ventas
 *   GET /api/ventas/{id} - Obtiene una venta específica
 *   POST /api/ventas - Crea una nueva venta
 *   PUT /api/ventas/{id} - Actualiza una venta existente
 *   DELETE /api/ventas/{id} - Elimina una venta
 * 
 * 
 * Control de acceso:
 * Los endpoints de GET, POST y PUT requieren autenticación con rol ADMIN o USER.
 * El endpoint DELETE está disponible sin restricción de roles específica.
 * 
 * Procesamiento de ventas:
 * La creación de ventas implementa un proceso de validación en dos fases:
 * 
 *   FASE 1 - VALIDACIÓN: Verifica que todos los productos solicitados tengan stock suficiente
 *   FASE 2 - CREACIÓN: Crea los detalles de venta, decrementa cantidades, calcula totales con IVA
 * 
 * El IVA se aplica al 16% (factor 1.16) sobre cada detalle de venta.
 * 
 * @author Adrian
 * @version 1.0
 * @see ISaleService
 * @see SaleResponseDTO
 */
@RestController
@RequestMapping("/api/ventas")
public class SaleController {

    /**
     * Servicio para la gestión de lógica de negocio de ventas.
     * Se utiliza para operaciones CRUD y procesamiento de transacciones.
     */
    private ISaleService saleService;

    /**
     * Constructor que inyecta el servicio de ventas.
     * 
     * @param saleService servicio para gestionar la lógica de negocio de ventas
     */
    public SaleController(ISaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * Obtiene la lista de todas las ventas registradas en el sistema.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Retorna una lista completa de objetos {@link SaleResponseDTO} con información
     * de todas las ventas incluyendo sus detalles (productos, cantidades, precios).
     * Se realiza como consulta de solo lectura optimizando el acceso a la base de datos.
     * 
     * @return {@link ResponseEntity} con la lista de {@link SaleResponseDTO}
     *         con estado HTTP 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<SaleResponseDTO>> findAllSales() {
        return ResponseEntity.ok(saleService.findAllSales());
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
     * @return {@link ResponseEntity} con el {@link SaleResponseDTO} correspondiente
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la venta con el ID especificado no existe
     * 
     * @see SaleResponseDTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<SaleResponseDTO> findByIdSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findByIdSale(id));
    }

    /**
     * Crea una nueva venta con los productos solicitados.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Implementa un proceso de validación en dos fases:
     * 
     *   FASE 1 (VALI/strong> Verifica que todos los productos tengan stock suficiente.
     *        Si algún producto no tiene suficiente cantidad, lanza excepción y no se crea la venta.
     *   FASE 2 (CREAtrong> Si la validación pasa, crea los detalles de venta,
     *        decrementa las cantidades en inventario, calcula subtotales y aplica IVA (factor 1.16).
     * 
     * 
     * El total de la venta se calcula sumando todos los subtotales de detalles con IVA aplicado.
     * 
     * Si la creación es exitosa, retorna la ubicación del nuevo recurso
     * en el encabezado "Location".
     * 
     * @param saleDTO objeto {@link SaleRequestDTO} con los datos de la venta a crear
     *                 (sucursalId, lista de productos con cantidades)
     * @return {@link ResponseEntity} con el {@link SaleResponseDTO} creado
     *         con estado HTTP 201 (Created) y encabezado Location
     * @throws IllegalArgumentException si los datos de la venta son inválidos
     * @throws NotFoundException si la sucursal o algún producto no existen
     * @throws RuntimeException si algún producto no tiene stock suficiente
     * 
     * @see SaleRequestDTO
     * @see SaleResponseDTO
     * @see ISaleService#crearVenta(SaleRequestDTO)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> create(@Valid @RequestBody SaleRequestDTO saleDTO) {
        SaleResponseDTO created = saleService.createSale(saleDTO);
        return ResponseEntity.created(URI.create("/api/ventas/" + created.getId())).body(created);
    }

    /**
     * Actualiza una venta existente.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * Permite actualizar los siguientes campos de una venta:
     * 
     *   Fecha de la venta
     *   Estado de la venta (PENDIENTE, PAGADA, CANCELADA)
     *   Sucursal asociada
     *   Total de la venta
     * 
     * 
     * Solo actualiza los campos proporcionados en el DTO. Los campos no especificados
     * (null) no se modifican, permitiendo actualizaciones parciales.
     * 
     * Nota:</strontalles de la venta (productos) no se pueden actualizar
     * directamente a través de este endpoint. Para cambiar los productos, debe eliminar
     * y crear una nueva venta.
     * 
     * @param id identificador único de la venta a actualizar
     * @param saleDTO objeto {@link SaleUpdateRequestDTO} con los datos a actualizar
     * @return {@link ResponseEntity} con el {@link SaleResponseDTO} actualizado
     *         con estado HTTP 200 (OK)
     * @throws NotFoundException si la venta no existe
     * 
     * @see SaleUpdateRequestDTO
     * @see SaleResponseDTO
     * @see ISaleService#actualizarVenta(Long, SaleUpdateRequestDTO)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @Valid @RequestBody SaleUpdateRequestDTO saleDTO) {
        return ResponseEntity.ok(saleService.updateSale(id, saleDTO));
    }

    /**
     * Elimina una venta específica del sistema.
     * 
     * Esta operación realiza una eliminación física: elimina completamente la venta
     * de la base de datos, incluyendo todos sus detalles de venta.
     * 
     * Advertencia: La eliminación de una venta NO restaura el inventario
     * de los productos. Las cantidades decrementadas durante la creación de la venta
     * no serán devueltas al stock de productos.
     * 
     * @param id identificador único de la venta a eliminar
     * @return {@link ResponseEntity} sin contenido con estado HTTP 204 (No Content)
     * @throws NotFoundException si la venta no existe
     * 
     * @see ISaleService#eliminarVenta(Long)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

}
