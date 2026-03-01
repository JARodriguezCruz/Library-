package com.biblioteca.observer;

import com.biblioteca.model.Libro;

public interface PrestamoObserver {
    void onPrestamo(Libro libro);
    void onDevolucion(Libro libro);
}
