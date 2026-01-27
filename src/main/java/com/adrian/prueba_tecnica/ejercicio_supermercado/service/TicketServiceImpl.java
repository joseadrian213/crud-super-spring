package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.venta.SaleResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.NotFoundException;
import com.adrian.prueba_tecnica.ejercicio_supermercado.mapper.Mapper;
import com.adrian.prueba_tecnica.ejercicio_supermercado.repository.SaleRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de generación de tickets en formato PDF.
 * 
 * Esta clase proporciona la funcionalidad de generar archivos PDF con los detalles
 * de una venta. Utiliza Thymeleaf para procesar plantillas HTML y OpenHTMLtoPDF
 * para convertir el contenido HTML a formato PDF.
 * 
 * El proceso general es:
 * 1. Buscar la venta en la base de datos por su ID
 * 2. Convertir la entidad Venta a DTO
 * 3. Procesar la plantilla HTML de Thymeleaf con los datos de la venta
 * 4. Convertir el HTML procesado a PDF usando OpenHTMLtoPDF
 * 5. Retornar el PDF como array de bytes
 * 
 * @author Adrian
 * @version 1.0
 * @see ITicketService
 * @see SaleRepository
 */
@Slf4j
@Service
public class TicketServiceImpl implements ITicketService {

    /**
     * Motor de plantillas Thymeleaf para procesar plantillas HTML.
     * Se utiliza para renderizar la plantilla de ticket con datos de venta.
     */
    private final TemplateEngine templateEngine;

    /**
     * Repositorio para acceder a la información de ventas en la base de datos.
     */
    private final SaleRepository saleRepository;

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param saleRepository repositorio para acceder a datos de ventas
     * @param templateEngine motor de plantillas Thymeleaf
     */
    public TicketServiceImpl(SaleRepository saleRepository, TemplateEngine templateEngine) {
        this.saleRepository = saleRepository;
        this.templateEngine = templateEngine;
    }

    /**
     * Genera un archivo PDF con el ticket de una venta específica.
     * 
     * Este método realiza los siguientes pasos:
     * 1. Busca la venta en la base de datos por su identificador
     * 2. Convierte la entidad Venta a DTO usando el Mapper
     * 3. Prepara el contexto de Thymeleaf con los datos de la venta
     * 4. Procesa la plantilla HTML ubicada en 'templates/tickets/ticket-venta.html'
     * 5. Convierte el HTML resultante a formato PDF usando OpenHTMLtoPDF
     * 6. Retorna el PDF como array de bytes en memoria
     * 
     * La plantilla HTML se encuentra en {@code src/main/resources/templates/tickets/ticket-venta.html}
     * 
     * @param id identificador único de la venta para la cual generar el ticket
     * @return array de bytes que contiene el contenido del archivo PDF
     * @throws NotFoundException si la venta con el ID especificado no existe
     * @throws RuntimeException si ocurre un error durante la generación del PDF
     * 
     * @see SaleResponseDTO
     * @see Mapper
     */
    @Override
    public byte[] generateTicketPDF(Long id) {
        // 1. Buscamos la venta y convertimos a DTO usando el mapper
        SaleResponseDTO sale = saleRepository.findById(id)
                .map(Mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("No se encontró la venta con ID: " + id));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 2. Preparamos el contexto de Thymeleaf que contendrá los datos de la venta
            Context context = new Context();
            context.setVariable("sale", sale);

            // 3. Renderizamos el HTML desde la plantilla
            // Ubicación: src/main/resources/templates/tickets/ticket-venta.html
            String htmlContent = templateEngine.process("tickets/ticket-venta", context);

            // 4. Configuración y ejecución de OpenHTMLtoPDF para la creación del PDF
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(outputStream);
            builder.run();

            log.info("Ticket generado exitosamente para la venta ID: {}", id);
            // El PDF está construido en memoria y se retorna como array de bytes
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF para venta ID: {}", id, e);
            throw new RuntimeException("Error al generar el ticket", e);
        }
    }
}
