package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.time.LocalDate;

public interface IVentaReporteService {
    byte[] generarExcelIdSucursal(Long sucursalId);

    byte[] generarExcelIdSucursalFecha(Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin);

    byte[] obtenerSucursales();
}
