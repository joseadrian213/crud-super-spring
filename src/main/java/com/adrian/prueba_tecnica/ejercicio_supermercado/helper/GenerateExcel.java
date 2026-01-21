package com.adrian.prueba_tecnica.ejercicio_supermercado.helper;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.DetalleVentaResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.VentaResponseDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase auxiliar para generar reportes en formato Excel.
 * 
 * Esta clase proporciona funcionalidades para convertir datos de ventas
 * en documentos Excel estructurados y formateados visualmente.
 * Utiliza Apache POI para la creación y manipulación de archivos Excel.
 * 
 * Características:
 * - Generación de archivos Excel en formato XLSX (Excel 2007+)
 * - Formateo automático de encabezados con color verde y texto blanco
 * - Alternancia de colores en filas (verde pastel) para mejor legibilidad
 * - Autoajuste automático de ancho de columnas
 * - Manejo de excepciones con logging
 * 
 * El archivo Excel generado contiene las siguientes columnas:
 * Id Venta, Fecha, Estado, Sucursal, Producto, Cantidad, Precio, Subtotal, Total
 * 
 * @author Adrian
 * @version 1.0
 */
@Slf4j
public class GenerateExcel {
    /**
     * Genera un archivo Excel con los datos de ventas.
     * 
     * Este método procesa una lista de ventas y sus detalles para crear un documento
     * Excel con formato profesional. Incluye:
     * - Encabezados con fondo verde y texto blanco en negrita
     * - Filas con colores alternados (verde claro y verde más oscuro)
     * - Autoajuste automático del ancho de columnas
     * - Una fila por detalle de venta (pueden haber múltiples detalles por venta)
     * 
     * Estructura del Excel:
     * Fila 0: Encabezados (Id Venta, Fecha, Estado, Sucursal, Producto, Cant, Precio, Subtotal, Total)
     * Fila 1+: Datos de ventas (una fila por cada detalle de venta)
     * 
     * @param ventas lista de {@link VentaResponseDTO} con los datos a incluir en el Excel
     * @param nombreHoja nombre de la hoja de cálculo a crear en el libro
     * @return array de bytes que contiene el contenido del archivo Excel generado
     * @throws RuntimeException si ocurre un error durante la generación del Excel
     * 
     * @see VentaResponseDTO
     * @see DetalleVentaResponseDTO
     */
    public static byte[] generarExcel(List<VentaResponseDTO> ventas, String nombreHoja) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(nombreHoja);

            // 1. DEFINICIÓN DE COLORES Y ESTILOS
            // Encabezado: Verde fuerte con texto blanco en negrita
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Estilos para filas alternas (Verde Pastel)
            XSSFCellStyle lightGreenStyle = workbook.createCellStyle();
            lightGreenStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 245, 220), null));
            lightGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle darkerGreenStyle = workbook.createCellStyle();
            darkerGreenStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(190, 230, 190), null));
            darkerGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 2. CREAR ENCABEZADOS
            Row header = sheet.createRow(0);
            String[] columns = { "Id Venta",
                    "Fecha",
                    "Estado",
                    "Sucursal",
                    "Producto",
                    "Cant",
                    "Precio",
                    "Subtotal",
                    "Total" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. LLENAR DATOS CON COLORES ALTERNOS
            int rowNum = 1;
            for (VentaResponseDTO venta : ventas) {
                // Crear una fila por cada detalle de venta
                for (DetalleVentaResponseDTO det : venta.getDetalle()) {
                    Row row = sheet.createRow(rowNum++);
                    // Elegir estilo según si la fila es par o impar
                    CellStyle currentStyle = (rowNum % 2 == 0) ? lightGreenStyle : darkerGreenStyle;

                    createStyledCell(row, 0, venta.getId().toString(), currentStyle);
                    createStyledCell(row, 1, venta.getFecha().toString(), currentStyle);
                    createStyledCell(row, 2, venta.getEstado().name(), currentStyle);
                    createStyledCell(row, 3, venta.getIdSucursal(), currentStyle);

                    createStyledCell(row, 4, det.getNombreProd(), currentStyle);
                    createStyledCell(row, 5, det.getCantProd().doubleValue(), currentStyle);
                    createStyledCell(row, 6, det.getPrecio().doubleValue(), currentStyle);
                    createStyledCell(row, 7, det.getSubtotal().doubleValue(), currentStyle);

                    createStyledCell(row, 8, venta.getTotal().doubleValue(), currentStyle);
                }
            }

            // 4. AUTOAJUSTE DE ANCHO DE COLUMNAS
            for (int i = 0; i < columns.length; i++)
                sheet.autoSizeColumn(i);

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    /**
     * Método auxiliar para crear celdas con estilo sin repetir código.
     * 
     * Crea una celda en la posición especificada de una fila, establece su valor
     * y aplica el estilo proporcionado. Maneja automáticamente la conversión de
     * valores numéricos (Double) y valores de objeto.
     * 
     * @param row la fila donde se creará la celda
     * @param column índice de la columna (0-based)
     * @param value valor a establecer en la celda (puede ser Double o cualquier Object)
     * @param style estilo {@link CellStyle} a aplicar a la celda
     */
    private static void createStyledCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Double)
            cell.setCellValue((Double) value);
        else
            cell.setCellValue(value.toString());
        cell.setCellStyle(style);
    }
}