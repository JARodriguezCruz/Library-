package com.biblioteca.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutorTest {

    @Test
    void getNombreCompleto_debeConcatenarNombreYApellido() {
        Autor autor = new Autor("Gabriel", "García Márquez", "Bio");
        assertEquals("Gabriel García Márquez", autor.getNombreCompleto());
    }

    @Test
    void agregarLibro_debeIncluirElLibroEnLaLista() {
        Autor autor = new Autor("Isabel", "Allende", "Bio");
        Libro libro = new Libro("La Casa de los Espíritus", autor, 1982, "ISBN-IA-001");
        autor.agregarLibro(libro);
        assertEquals(1, autor.getLibros().size());
    }

    @Test
    void agregarLibro_noDuplicaElMismoLibro() {
        Autor autor = new Autor("Pablo", "Neruda", "Bio");
        Libro libro = new Libro("Veinte poemas", autor, 1924, "ISBN-PN-001");
        autor.agregarLibro(libro);
        autor.agregarLibro(libro);
        assertEquals(1, autor.getLibros().size());
    }

    @Test
    void constructorConId_debeAsignarTodosLosCampos() {
        Autor autor = new Autor(7, "Julio", "Cortázar", "Escritor argentino");
        assertEquals(7,                    autor.getId());
        assertEquals("Julio",              autor.getNombre());
        assertEquals("Cortázar",           autor.getApellido());
        assertEquals("Escritor argentino", autor.getBiografia());
    }

    @Test
    void libroNuevo_estaDisponiblePorDefecto() {
        Autor autor = new Autor("Test", "Autor", "Bio");
        Libro libro = new Libro("Titulo", autor, 2000, "ISBN-X");
        assertTrue(libro.isDisponible());
    }
}
