package com.biblioteca.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void constructorCompleto_asignaTodosLosCampos() {
        Usuario u = new Usuario(1, "Ana", "ana@mail.com", "pass");
        assertEquals(1,              u.getId());
        assertEquals("Ana",          u.getNombre());
        assertEquals("ana@mail.com", u.getEmail());
        assertEquals("pass",         u.getPassword());
    }

    @Test
    void getLibrosPrestados_inicialmenteVacio() {
        Usuario u = new Usuario("Carlos", "carlos@mail.com", "123");
        assertNotNull(u.getLibrosPrestados());
        assertTrue(u.getLibrosPrestados().isEmpty());
    }

    @Test
    void setters_funcionanCorrectamente() {
        Usuario u = new Usuario();
        u.setId(3);
        u.setNombre("María");
        u.setEmail("maria@mail.com");
        u.setPassword("clave");
        assertEquals(3,               u.getId());
        assertEquals("María",         u.getNombre());
        assertEquals("maria@mail.com",u.getEmail());
        assertEquals("clave",         u.getPassword());
    }
}
