package com.adrian.prueba_tecnica.ejercicio_supermercado.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.adrian.prueba_tecnica.ejercicio_supermercado.dto.response.ErrorResponseDTO;

@RestControllerAdvice
public class HandlerExceptionController {
    @ExceptionHandler({ ArithmeticException.class })

    public ResponseEntity<ErrorResponseDTO> divisionByZero(Exception e) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setDate(new Date());
        error.setError("Error division by zero");
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(error);

    }

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> notFound(Exception e) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setDate(new Date());
        error.setError("API REST no encontrada");
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
