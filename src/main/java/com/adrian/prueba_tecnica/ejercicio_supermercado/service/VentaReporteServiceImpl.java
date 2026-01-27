package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.helper.GenerateExcel;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.SaleRepository;

/**
 * Implementación del servicio de generación de reportes de ventas en formato
 * Excel.
 * 
 * Esta clase proporciona la funcionalidad para generar reportes de ventas en
 * formato Excel,
 * permitiendo filtrar por sucursal, rango de fechas, o consultar todas las
 * ventas.
 * Implementa la interfaz {@link ISaleReportService}.
 * 
 * Características:
 * - Generación de reportes Excel de ventas por sucursal
 * - Generación de reportes Excel con filtros por sucursal y rango de fechas
 * - Generación de reportes Excel con todas las ventas del sistema
 * - Inclusión automática de detalles de venta y productos
 * 
 * @author Adrian
 * @version 1.0
 * @see ISaleReportService
 * @see SaleRepository
 * @see GenerateExcel
 */
@Service
public class VentaReporteServiceImpl implements ISaleReportService {
    /**
     * Repositorio para acceder a la información de ventas en la base de datos.
     */
    private final SaleRepository saleRepository;

    /**
     * Constructor que inyecta el repositorio de ventas.
     * 
     * @param saleRepository repositorio para acceder a datos de ventas
     */
    public VentaReporteServiceImpl(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * Genera un reporte en formato Excel con todas las ventas de una sucursal
     * específica.
     * 
     * Este método obtiene todas las ventas asociadas a una sucursal, incluyendo
     * sus detalles y productos asociados, y genera un archivo Excel con esta
     * información.
     * 
     * @param sucursalId identificador único de la sucursal para la cual generar el
     *                   reporte
     * @return array de bytes que contiene el contenido del archivo Excel generado
     * 
     * @see GenerateExcel#generarExcel(List, String)
     */
    @Override
    public byte[] generateExcelIdBranch(Long sucursalId) {
        List<SaleResponseDTO> ventas = saleRepository.findByBranchIdWithDetails(sucursalId).stream()
                .map(Mapper::toDTO)
                .toList();
        return GenerateExcel.generarExcel(ventas, "Ventas - Sucursal " + sucursalId);
    }

    /**
     * Genera un reporte en formato Excel con ventas de una sucursal dentro de un
     * rango de fechas.
     * 
     * Este método obtiene todas las ventas de una sucursal específica que
     * ocurrieron
     * entre dos fechas (inclusive), incluyendo sus detalles y productos, y genera
     * un archivo Excel con esta información.
     * 
     * @param branchId  identificador único de la sucursal
     * @param startDate fecha de inicio del rango (inclusive)
     * @param endDate   fecha de fin del rango (inclusive)
     * @return array de bytes que contiene el contenido del archivo Excel generado
     * 
     * @see GenerateExcel#generarExcel(List, String)
     */
    @Override
    public byte[] generateExcelIdBranchDate(Long branchId, LocalDate startDate, LocalDate endDate) {
        List<SaleResponseDTO> ventas = saleRepository
                .findByBranchIdAndDateBetweenWithDetails(branchId, startDate, endDate).stream()
                .map(Mapper::toDTO)
                .toList();
        return GenerateExcel.generarExcel(ventas,
                "vent-suc-" + branchId + "(" + startDate + "-" + endDate + ")");
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
    public byte[] getBranches() {
        List<SaleResponseDTO> ventas = saleRepository.findAllWithDetailAndProduct().stream().map(Mapper::toDTO)
                .toList();
        return GenerateExcel.generarExcel(ventas, "Ventas - Todas las sucursales");

    }

}
