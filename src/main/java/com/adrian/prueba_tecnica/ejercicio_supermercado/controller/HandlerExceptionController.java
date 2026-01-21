package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.ErrorResponseDTO;
import com.adrian.prueba_tecnica.ejercicio_supermercado.exception.UserNotFoundException;

/**
 * Controlador centralizado para el manejo de excepciones en la API REST.
 * 
 * Esta clase implementa un mecanismo global de manejo de excepciones para toda
 * la aplicación utilizando {@link RestControllerAdvice}. Captura diferentes tipos
 * de excepciones y las convierte en respuestas HTTP consistentes y bien formateadas.
 * 
 * Excepciones manejadas:
 * - {@link ArithmeticException}: Errores de cálculos matemáticos
 * - {@link NumberFormatException}: Errores al convertir números
 * - {@link NotFoundException}: Recurso no encontrado (404)
 * - {@link MethodArgumentNotValidException}: Errores de validación en argumentos
 * - {@link NullPointerException}: Referencias nulas
 * - {@link HttpMessageNotWritableException}: Errores al serializar respuesta
 * - {@link UserNotFoundException}: Usuario o rol no encontrado
 * 
 * Todas las respuestas de error incluyen:
 * - Timestamp de la ocurrencia
 * - Descripción del error
 * - Mensaje de excepción
 * - Código de estado HTTP
 * 
 * @author Adrian
 * @version 1.0
 */
@RestControllerAdvice
public class HandlerExceptionController {
    /**
     * Maneja excepciones de división por cero y operaciones aritméticas inválidas.
     * 
     * Retorna un código de estado 500 (Internal Server Error) con los detalles
     * del error en un objeto {@link ErrorResponseDTO}.
     * 
     * @param e la excepción {@link ArithmeticException} capturada
     * @return {@link ResponseEntity} con el error formateado y estado 500
     */
    @ExceptionHandler({ ArithmeticException.class })

    public ResponseEntity<ErrorResponseDTO> divisionByZero(Exception e) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setDate(new Date());
        error.setError("Error division by zero");
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(error);

    }

    /**
     * Maneja excepciones de formato incorrecto al convertir números.
     * 
     * Retorna un código de estado 400 (Bad Request) con un mapa de errores.
     * Se utiliza cuando una cadena no puede ser convertida a número.
     * 
     * @param e la excepción {@link NumberFormatException} capturada
     * @return {@link Map} con los detalles del error y estado 400
     */
    @ExceptionHandler({ NumberFormatException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> numberFormatException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("date", new Date());
        error.put("error", "Error de formato de número");
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return error;
    }

    /**
     * Maneja excepciones cuando un recurso no es encontrado.
     * 
     * Retorna un código de estado 404 (Not Found) con los detalles del error
     * en un objeto {@link ErrorResponseDTO}.
     * 
     * @param e la excepción {@link NotFoundException} capturada
     * @return {@link ResponseEntity} con el error formateado y estado 404
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> notFound(Exception e) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setDate(new Date());
        error.setError("API REST no encontrada");
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja excepciones de validación en los argumentos de solicitud.
     * 
     * Retorna un código de estado 400 (Bad Request) con un mapa que contiene
     * los nombres de los campos y sus respectivos mensajes de validación.
     * Se dispara cuando la validación de anotaciones (ej: @Valid) falla.
     * 
     * @param ex la excepción {@link MethodArgumentNotValidException} capturada
     * @return {@link ResponseEntity} con un mapa de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Extraer los errores de validación y agruparlos por campo
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Maneja excepciones relacionadas con referencias nulas, serialización
     * de respuestas y usuarios no encontrados.
     * 
     * Retorna un código de estado 500 (Internal Server Error) con un mapa de errores.
     * Combina múltiples tipos de excepciones en un único manejador.
     * 
     * Excepciones manejadas:
     * - {@link NullPointerException}: Intento de usar un objeto nulo
     * - {@link HttpMessageNotWritableException}: Error al serializar la respuesta
     * - {@link UserNotFoundException}: Usuario o rol no existe en el sistema
     * 
     * @param e la excepción capturada (uno de los tipos listados)
     * @return {@link Map} con los detalles del error y estado 500
     */
    @ExceptionHandler({ NullPointerException.class, HttpMessageNotWritableException.class,
            UserNotFoundException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> userNotFoundException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("date", new Date());
        error.put("error", "El usuario o role no existen");
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return error;
    }
}
