package com.biblioteca.observer;

import com.biblioteca.model.Libro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observador concreto que registra (log) los eventos de préstamo
 * y devolución de libros en la consola.
 */
public class LogPrestamoObserver implements PrestamoObserver {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onPrestamo(Libro libro) {
        System.out.printf("[LOG %s] 📖 Préstamo registrado: \"%s\" (ISBN: %s)%n",
                LocalDateTime.now().format(FMT), libro.getTitulo(), libro.getIsbn());
    }

    @Override
    public void onDevolucion(Libro libro) {
        System.out.printf("[LOG %s] ✅ Devolución registrada: \"%s\" (ISBN: %s)%n",
                LocalDateTime.now().format(FMT), libro.getTitulo(), libro.getIsbn());
    }
}
