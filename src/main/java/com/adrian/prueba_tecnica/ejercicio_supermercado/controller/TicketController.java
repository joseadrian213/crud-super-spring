package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.prueba_tecnica.ejercicio_supermercado.service.ITicketService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST para la generación y descarga de tickets (recibos) de ventas en formato PDF.
 * 
 * Este controlador proporciona endpoints para generar y obtener archivos PDF de tickets
 * asociados a transacciones de ventas. Los tickets se generan dinámicamente usando
 * plantillas Thymeleaf y se convierten a PDF mediante OpenHTMLtoPDF.
 * 
 * <p><strong>Rutas de acceso:</strong></p>
 * <ul>
 *   <li>GET /api/ventas/ticket/{id} - Descarga el PDF del ticket de una venta específica
 * </ul>
 * 
 * <p><strong>Control de acceso:</strong></p>
 * Los endpoints requieren autenticación de usuario con roles ADMIN o USER.
 * 
 * @author Adrian
 * @version 1.0
 * @see ITicketService
 */
@RestController
@RequestMapping("/api/ventas")
public class TicketController {
    
    /**
     * Servicio para la generación de tickets PDF de ventas.
     * Se utiliza para generar dinámicamente los recibos en formato PDF.
     */
    private final ITicketService ticketService;

    /**
     * Constructor que inyecta el servicio de tickets.
     * 
     * @param ticketService servicio para gestionar la generación de tickets PDF
     */
    public TicketController(ITicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Obtiene el PDF del ticket (recibo) asociado a una venta específica.
     * 
     * Genera dinámicamente un documento PDF que contiene todos los detalles de la venta:
     * fecha, productos, cantidades, precios, subtotal e IVA, y total. El PDF se envía
     * como descargar inline (se abre en el navegador) con control de caché.
     * 
     * Requiere permisos: ADMIN o USER
     * 
     * El archivo PDF se nombra con el patrón: ticket-venta-{id}.pdf
     * 
     * @param id identificador único de la venta para la cual generar el ticket
     * @return {@link ResponseEntity} con el contenido PDF en formato byte[]
     *         con estado HTTP 200 (OK) y encabezados de PDF configurados:
     *         <ul>
     *           <li>Content-Type: application/pdf</li>
     *           <li>Content-Disposition: inline; filename=ticket-venta-{id}.pdf</li>
     *           <li>Cache-Control: must-revalidate, post-check=0, pre-check=0</li>
     *         </ul>
     * @throws NotFoundException si la venta con el ID especificado no existe
     * @throws RuntimeException si ocurre un error durante la generación del PDF
     * 
     * @see ITicketService#generarTicketPDF(Long)
     */
    @GetMapping("/ticket/{id}")
    public ResponseEntity<byte[]> getTicket(@PathVariable Long id) {
        byte[] pdfContent = ticketService.generarTicketPDF(id);

        // Configuración de encabezados HTTP para respuesta PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // "inline" para que se abra en el navegador, "attachment" para descarga directa
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("ticket-venta-" + id + ".pdf")
                .build());
        
        // Control de caché para garantizar que el navegador obtenga la versión más reciente
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}
