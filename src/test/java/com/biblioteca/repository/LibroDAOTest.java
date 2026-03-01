package com.biblioteca.repository;

import com.biblioteca.H2TestBase;
import com.biblioteca.model.Autor;
import com.biblioteca.model.Libro;
import org.junit.jupiter.api.*;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibroDAOTest extends H2TestBase {

    private final AutorDAO autorDAO = new AutorDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private Autor autorBase;

    @BeforeEach
    void limpiar() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM libro");
            st.execute("DELETE FROM autor");
            st.execute("ALTER TABLE libro  ALTER COLUMN id RESTART WITH 1");
            st.execute("ALTER TABLE autor  ALTER COLUMN id RESTART WITH 1");
        }
        autorBase = new Autor("Test", "Autor", "Bio de prueba");
        autorDAO.crear(autorBase);
    }

    @Test
    @Order(1)
    void crear_debeAsignarId() throws Exception {
        Libro libro = new Libro("Don Quijote", autorBase, 1605, "ISBN-DQ-001");
        libroDAO.crear(libro);
        assertTrue(libro.getId() > 0);
    }

    @Test
    @Order(2)
    void listar_debeRetornarLibrosConAutorPopulado() throws Exception {
        libroDAO.crear(new Libro("Libro A", autorBase, 2000, "ISBN-A"));
        libroDAO.crear(new Libro("Libro B", autorBase, 2001, "ISBN-B"));

        List<Libro> lista = libroDAO.listar();
        assertEquals(2, lista.size());
        assertNotNull(lista.get(0).getAutor());
        assertEquals("Test", lista.get(0).getAutor().getNombre());
    }

    @Test
    @Order(3)
    void buscarPorIsbn_debeEncontrarElLibro() throws Exception {
        libroDAO.crear(new Libro("El Aleph", autorBase, 1949, "ISBN-ALEPH"));
        Optional<Libro> encontrado = libroDAO.buscarPorIsbn("ISBN-ALEPH");
        assertTrue(encontrado.isPresent());
        assertEquals("El Aleph", encontrado.get().getTitulo());
    }

    @Test
    @Order(4)
    void actualizarDisponibilidad_debeCambiarElEstado() throws Exception {
        Libro libro = new Libro("Prestable", autorBase, 2020, "ISBN-PREST");
        libroDAO.crear(libro);
        assertTrue(libro.isDisponible());

        libroDAO.actualizarDisponibilidad(libro.getId(), false);

        Libro actualizado = libroDAO.buscarPorId(libro.getId()).orElseThrow();
        assertFalse(actualizado.isDisponible());
    }

    @Test
    @Order(5)
    void actualizar_debeCambiarTitulo() throws Exception {
        Libro libro = new Libro("Titulo viejo", autorBase, 1999, "ISBN-OLD");
        libroDAO.crear(libro);

        libro.setTitulo("Titulo nuevo");
        libroDAO.actualizar(libro);

        Libro actualizado = libroDAO.buscarPorId(libro.getId()).orElseThrow();
        assertEquals("Titulo nuevo", actualizado.getTitulo());
    }

    @Test
    @Order(6)
    void eliminar_debeRemoverElLibro() throws Exception {
        Libro libro = new Libro("Eliminar", autorBase, 2000, "ISBN-DEL");
        libroDAO.crear(libro);
        int id = libro.getId();

        libroDAO.eliminar(id);

        assertFalse(libroDAO.buscarPorId(id).isPresent());
    }
}
