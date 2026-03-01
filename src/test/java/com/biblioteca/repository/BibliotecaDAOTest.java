package com.biblioteca.repository;

import com.biblioteca.H2TestBase;
import com.biblioteca.model.Biblioteca;
import org.junit.jupiter.api.*;

import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BibliotecaDAOTest extends H2TestBase {

    private final BibliotecaDAO dao = new BibliotecaDAO();

    @BeforeEach
    void limpiar() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM biblioteca");
            st.execute("ALTER TABLE biblioteca ALTER COLUMN id RESTART WITH 1");
        }
    }

    @Test
    @Order(1)
    void crear_debeAsignarId() throws Exception {
        Biblioteca b = new Biblioteca("Central", "Calle 1", "555-0000");
        dao.crear(b);
        assertTrue(b.getId() > 0);
    }

    @Test
    @Order(2)
    void listar_debeRetornarTodasLasSedes() throws Exception {
        dao.crear(new Biblioteca("Norte", "Calle 2", "555-1111"));
        dao.crear(new Biblioteca("Sur",   "Calle 3", "555-2222"));
        List<Biblioteca> lista = dao.listar();
        assertEquals(2, lista.size());
    }

    @Test
    @Order(3)
    void buscarPorId_debeRetornarLaSedeCrrecta() throws Exception {
        Biblioteca b = new Biblioteca("Este", "Calle 4", "555-3333");
        dao.crear(b);
        Biblioteca encontrada = dao.buscarPorId(b.getId()).orElseThrow();
        assertEquals("Este", encontrada.getNombre());
    }

    @Test
    @Order(4)
    void actualizar_debeCambiarNombre() throws Exception {
        Biblioteca b = new Biblioteca("Vieja", "Dir", "555");
        dao.crear(b);
        b.setNombre("Nueva");
        dao.actualizar(b);
        assertEquals("Nueva", dao.buscarPorId(b.getId()).orElseThrow().getNombre());
    }

    @Test
    @Order(5)
    void eliminar_debeRemoverLaSede() throws Exception {
        Biblioteca b = new Biblioteca("Temporal", "Dir", "555");
        dao.crear(b);
        dao.eliminar(b.getId());
        assertFalse(dao.buscarPorId(b.getId()).isPresent());
    }
}
