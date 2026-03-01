package com.biblioteca.exceptions;

public class UsuarioNoRegistradoException extends RuntimeException {
    public UsuarioNoRegistradoException(String mensaje) {
        super(mensaje);
    }
}
