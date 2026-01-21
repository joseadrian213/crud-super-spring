package com.adrian.prueba_tecnica.ejercicio_supermercado.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
