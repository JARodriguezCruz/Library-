package com.biblioteca.repository;

import com.biblioteca.H2TestBase;
import com.biblioteca.model.Autor;
import org.junit.jupiter.api.*;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutorDAOTest extends H2TestBase {

    private final AutorDAO dao = new AutorDAO();

    @BeforeEach
    void limpiarTabla() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM autor");
            st.execute("ALTER TABLE autor ALTER COLUMN id RESTART WITH 1");
        }
    }

    @Test
    @Order(1)
    void crear_debeAsignarIdAutoGenerado() throws Exception {
        Autor autor = new Autor("García", "Lorca", "Poeta español");
        dao.crear(autor);
        assertTrue(autor.getId() > 0, "El id debería ser mayor a 0 tras la inserción");
    }

    @Test
    @Order(2)
    void listar_debeRetornarAutoresInsertados() throws Exception {
        dao.crear(new Autor("Pablo", "Neruda", "Poeta chileno"));
        dao.crear(new Autor("Octavio", "Paz", "Poeta mexicano"));

        List<Autor> lista = dao.listar();
        assertEquals(2, lista.size());
    }

    @Test
    @Order(3)
    void buscarPorId_debeRetornarElAutorCorrecto() throws Exception {
        Autor autor = new Autor("Rulfo", "Juan", "Escritor mexicano");
        dao.crear(autor);

        Optional<Autor> encontrado = dao.buscarPorId(autor.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Rulfo", encontrado.get().getNombre());
    }

    @Test
    @Order(4)
    void buscarPorId_debeRetornarVacioSiNoExiste() throws Exception {
        Optional<Autor> resultado = dao.buscarPorId(9999);
        assertFalse(resultado.isPresent());
    }

    @Test
    @Order(5)
    void actualizar_debeCambiarLosCampos() throws Exception {
        Autor autor = new Autor("Original", "Apellido", "Bio original");
        dao.crear(autor);

        autor.setNombre("Actualizado");
        autor.setBiografia("Nueva bio");
        dao.actualizar(autor);

        Autor actualizado = dao.buscarPorId(autor.getId()).orElseThrow();
        assertEquals("Actualizado", actualizado.getNombre());
        assertEquals("Nueva bio",   actualizado.getBiografia());
    }

    @Test
    @Order(6)
    void eliminar_debeRemoverElRegistro() throws Exception {
        Autor autor = new Autor("Temporal", "Borrar", "Bio");
        dao.crear(autor);
        int id = autor.getId();

        dao.eliminar(id);

        assertFalse(dao.buscarPorId(id).isPresent());
    }
}
