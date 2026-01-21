package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.service.IVentaReporteService;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST para la generación de reportes de ventas en formato Excel.
 * 
 * Proporciona endpoints para descargar reportes de ventas en formato Excel (XLSX)
 * con diferentes criterios de filtrado. Los archivos se generan dinámicamente
 * y se envían como descargas en el navegador.
 * 
 * Endpoints disponibles:
 * - GET /api/ventas/reporte/excel - Reporte de todas las ventas
 * - GET /api/ventas/reporte/excel/sucursal/{id} - Reporte de ventas por sucursal
 * - GET /api/ventas/reporte/excel/sucursal/{id}/{fechaInicio}/{fechaFin} - 
 *   Reporte de ventas por sucursal y rango de fechas
 * 
 * Todos los reportes se descargan como archivos Excel (XLSX) con formato profesional
 * incluyendo encabezados con colores y datos tabulados.
 * 
 * @author Adrian
 * @version 1.0
 * @see IVentaReporteService
 */
@RestController
@RequestMapping("/api/ventas/reporte")
public class ReporteVentaController {
    /**
     * Servicio para la generación de reportes de ventas en formato Excel.
     */
    IVentaReporteService ventaReporteService;

    /**
     * Constructor que inyecta el servicio de reportes de ventas.
     * 
     * @param ventaReporteService servicio para generar reportes de ventas en Excel
     */
    public ReporteVentaController(IVentaReporteService ventaReporteService) {
        this.ventaReporteService = ventaReporteService;
    }

    /**
     * Genera un reporte Excel con todas las ventas del sistema.
     * 
     * Descarga un archivo Excel formateado que contiene todas las ventas
     * registradas en el sistema, incluyendo detalles de cada venta y productos.
     * 
     * @return {@link ResponseEntity} con el archivo Excel en bytes,
     *         encabezados apropiados para descarga (Content-Type: application/octet-stream)
     *         y estado HTTP 200 (OK)
     */
    @GetMapping("/excel")
    public ResponseEntity<byte[]> findAll() {
        byte[] excel = ventaReporteService.obtenerSucursales();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("reporte-ventas.xlsx").build());
        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }

    /**
     * Genera un reporte Excel con todas las ventas de una sucursal específica.
     * 
     * Descarga un archivo Excel formateado que contiene todas las ventas
     * asociadas a una sucursal determinada.
     * 
     * @param id identificador único de la sucursal para filtrar las ventas
     * @return {@link ResponseEntity} con el archivo Excel en bytes,
     *         encabezados apropiados para descarga y estado HTTP 200 (OK)
     * @throws NotFoundException si la sucursal no existe
     */
    @GetMapping("/excel/sucursal/{id}")
    public ResponseEntity<byte[]> findReportByIdSucursal(@PathVariable Long id) {
        byte[] excel = ventaReporteService.generarExcelIdSucursal(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("ventas-sucursal-" + id + ".xlsx").build());
        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }

    /**
     * Genera un reporte Excel con ventas de una sucursal dentro de un rango de fechas.
     * 
     * Descarga un archivo Excel formateado que contiene todas las ventas
     * de una sucursal específica ocurridas entre dos fechas (inclusive).
     * 
     * Las fechas deben proporcionarse en formato ISO (yyyy-MM-dd).
     * Por ejemplo: /api/ventas/reporte/excel/sucursal/1/2024-01-01/2024-12-31
     * 
     * @param id identificador único de la sucursal
     * @param fechaInicio fecha de inicio del rango (formato: yyyy-MM-dd, inclusive)
     * @param fechaFin fecha de fin del rango (formato: yyyy-MM-dd, inclusive)
     * @return {@link ResponseEntity} con el archivo Excel en bytes,
     *         encabezados apropiados para descarga y estado HTTP 200 (OK)
     * @throws NotFoundException si la sucursal no existe
     */
    @GetMapping("/excel/sucursal/{id}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<byte[]> findReportByIdSucursalBeetwenFecha(@PathVariable Long id,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        byte[] excel = ventaReporteService.generarExcelIdSucursalFecha(id, fechaInicio, fechaFin);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("ventas-rango-fechas-sucrusal-" + id + ".xlsx").build());

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }

}
