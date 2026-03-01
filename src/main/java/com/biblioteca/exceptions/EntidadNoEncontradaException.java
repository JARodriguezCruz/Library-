package com.biblioteca.exceptions;

public class EntidadNoEncontradaException extends RuntimeException {
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
