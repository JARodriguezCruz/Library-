package com.biblioteca.repository;

import com.biblioteca.H2TestBase;
import com.biblioteca.model.Usuario;
import org.junit.jupiter.api.*;

import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioDAOTest extends H2TestBase {

    private final UsuarioDAO dao = new UsuarioDAO();

    @BeforeEach
    void limpiar() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM usuario");
            st.execute("ALTER TABLE usuario ALTER COLUMN id RESTART WITH 1");
        }
    }

    @Test
    @Order(1)
    void crear_debeAsignarId() throws Exception {
        Usuario u = new Usuario("Luis", "luis@mail.com", "pass");
        dao.crear(u);
        assertTrue(u.getId() > 0);
    }

    @Test
    @Order(2)
    void listar_debeRetornarUsuariosInsertados() throws Exception {
        dao.crear(new Usuario("Ana",  "ana@mail.com",  "a1"));
        dao.crear(new Usuario("Juan", "juan@mail.com", "j1"));
        List<Usuario> lista = dao.listar();
        assertEquals(2, lista.size());
    }

    @Test
    @Order(3)
    void buscarPorEmail_debeEncontrarAlUsuario() throws Exception {
        dao.crear(new Usuario("Pedro", "pedro@mail.com", "p1"));
        assertTrue(dao.buscarPorEmail("pedro@mail.com").isPresent());
    }

    @Test
    @Order(4)
    void actualizar_debeCambiarNombre() throws Exception {
        Usuario u = new Usuario("Viejo", "viejo@mail.com", "pass");
        dao.crear(u);
        u.setNombre("Nuevo");
        dao.actualizar(u);
        Usuario actualizado = dao.buscarPorId(u.getId()).orElseThrow();
        assertEquals("Nuevo", actualizado.getNombre());
    }

    @Test
    @Order(5)
    void eliminar_debeRemoverAlUsuario() throws Exception {
        Usuario u = new Usuario("Temporal", "tmp@mail.com", "pass");
        dao.crear(u);
        dao.eliminar(u.getId());
        assertFalse(dao.buscarPorId(u.getId()).isPresent());
    }
}
