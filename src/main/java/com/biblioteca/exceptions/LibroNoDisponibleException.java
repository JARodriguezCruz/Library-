package com.biblioteca.exceptions;

public class LibroNoDisponibleException extends RuntimeException {
    public LibroNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
