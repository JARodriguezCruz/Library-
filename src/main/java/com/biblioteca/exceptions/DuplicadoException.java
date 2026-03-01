package com.biblioteca.exceptions;

public class DuplicadoException extends RuntimeException {
    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}
