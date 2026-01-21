package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.helper.GenerateExcel;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.VentaRepository;

/**
 * Implementación del servicio de generación de reportes de ventas en formato Excel.
 * 
 * Esta clase proporciona la funcionalidad para generar reportes de ventas en formato Excel,
 * permitiendo filtrar por sucursal, rango de fechas, o consultar todas las ventas.
 * Implementa la interfaz {@link IVentaReporteService}.
 * 
 * Características:
 * - Generación de reportes Excel de ventas por sucursal
 * - Generación de reportes Excel con filtros por sucursal y rango de fechas
 * - Generación de reportes Excel con todas las ventas del sistema
 * - Inclusión automática de detalles de venta y productos
 * 
 * @author Adrian
 * @version 1.0
 * @see IVentaReporteService
 * @see VentaRepository
 * @see GenerateExcel
 */
@Service
public class VentaReporteServiceImpl implements IVentaReporteService {
    /**
     * Repositorio para acceder a la información de ventas en la base de datos.
     */
    private final VentaRepository ventaRepository;

    /**
     * Constructor que inyecta el repositorio de ventas.
     * 
     * @param ventaRepository repositorio para acceder a datos de ventas
     */
    public VentaReporteServiceImpl(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    /**
     * Genera un reporte en formato Excel con todas las ventas de una sucursal específica.
     * 
     * Este método obtiene todas las ventas asociadas a una sucursal, incluyendo
     * sus detalles y productos asociados, y genera un archivo Excel con esta información.
     * 
     * @param sucursalId identificador único de la sucursal para la cual generar el reporte
     * @return array de bytes que contiene el contenido del archivo Excel generado
     * 
     * @see GenerateExcel#generarExcel(List, String)
     */
    @Override
    public byte[] generarExcelIdSucursal(Long sucursalId) {
        List<VentaResponseDTO> ventas = ventaRepository.findBySucursalIdConDetalles(sucursalId).stream()
                .map(Mapper::toDTO)
                .toList();
        return GenerateExcel.generarExcel(ventas, "Ventas - Sucursal " + sucursalId);
    }

    /**
     * Genera un reporte en formato Excel con ventas de una sucursal dentro de un rango de fechas.
     * 
     * Este método obtiene todas las ventas de una sucursal específica que ocurrieron
     * entre dos fechas (inclusive), incluyendo sus detalles y productos, y genera
     * un archivo Excel con esta información.
     * 
     * @param sucursalId identificador único de la sucursal
     * @param fechaInicio fecha de inicio del rango (inclusive)
     * @param fechaFin fecha de fin del rango (inclusive)
     * @return array de bytes que contiene el contenido del archivo Excel generado
     * 
     * @see GenerateExcel#generarExcel(List, String)
     */
    @Override
    public byte[] generarExcelIdSucursalFecha(Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<VentaResponseDTO> ventas = ventaRepository
                .findBySucursalIdAndFechaBetweenConDetalles(sucursalId, fechaInicio, fechaFin).stream()
                .map(Mapper::toDTO)
                .toList();
        return GenerateExcel.generarExcel(ventas,
                "vent-suc-" + sucursalId + "(" + fechaInicio + "-" + fechaFin + ")");
    }

    /**
     * Genera un reporte en formato Excel con todas las ventas del sistema.
     * 
     * Este método obtiene todas las ventas registradas en el sistema, incluyendo
     * sus detalles y productos asociados, y genera un archivo Excel consolidado.
     * 
     * @return array de bytes que contiene el contenido del archivo Excel generado
     * 
     * @see GenerateExcel#generarExcel(List, String)
     */
    @Override
    public byte[] obtenerSucursales() {
        List<VentaResponseDTO> ventas = ventaRepository.findAllConDetalleYProducto().stream().map(Mapper::toDTO)
                .toList();
        return GenerateExcel.generarExcel(ventas, "Ventas - Todas las sucursales");

    }

}
