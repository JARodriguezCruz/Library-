package com.biblioteca.service;

import com.biblioteca.exceptions.DuplicadoException;
import com.biblioteca.exceptions.LibroNoDisponibleException;
import com.biblioteca.exceptions.UsuarioNoRegistradoException;
import com.biblioteca.model.Autor;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroDAO;
import com.biblioteca.repository.UsuarioDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BibliotecaServiceTest {

    @Mock private LibroDAO   libroDAO;
    @Mock private UsuarioDAO usuarioDAO;

    private BibliotecaService service;
    private Autor              autorBase;

    @BeforeEach
    void setUp() {
        service   = new BibliotecaService(libroDAO, usuarioDAO);
        autorBase = new Autor(1, "García", "Márquez", "Bio");
    }

    // ----------------------------------------------------------------
    // agregarLibro
    // ----------------------------------------------------------------

    @Test
    void agregarLibro_debeCrearCuandoIsbnNoExiste() throws Exception {
        Libro libro = new Libro("Titulo", autorBase, 2000, "ISBN-001");
        when(libroDAO.buscarPorIsbn("ISBN-001")).thenReturn(Optional.empty());

        service.agregarLibro(libro);

        verify(libroDAO, times(1)).crear(libro);
    }

    @Test
    void agregarLibro_debeLanzarDuplicadoExceptionSiIsbnYaExiste() throws Exception {
        Libro existente = new Libro(1, "Existente", autorBase, 1999, "ISBN-DUP", true);
        Libro nuevo     = new Libro("Nuevo",     autorBase, 2020, "ISBN-DUP");

        when(libroDAO.buscarPorIsbn("ISBN-DUP")).thenReturn(Optional.of(existente));

        assertThrows(DuplicadoException.class, () -> service.agregarLibro(nuevo));
        verify(libroDAO, never()).crear(any());
    }

    @Test
    void agregarLibro_debeLanzarExcepcionSiTituloEsNulo() {
        Libro libro = new Libro(null, autorBase, 2000, "ISBN-T");
        assertThrows(IllegalArgumentException.class, () -> service.agregarLibro(libro));
    }

    // ----------------------------------------------------------------
    // listarLibros
    // ----------------------------------------------------------------

    @Test
    void listarLibros_debeRetornarLaListaDelDAO() throws Exception {
        List<Libro> esperados = List.of(
                new Libro(1, "Libro A", autorBase, 2001, "A", true),
                new Libro(2, "Libro B", autorBase, 2002, "B", false)
        );
        when(libroDAO.listar()).thenReturn(esperados);

        List<Libro> resultado = service.listarLibros();

        assertEquals(2, resultado.size());
    }

    // ----------------------------------------------------------------
    // prestarLibro
    // ----------------------------------------------------------------

    @Test
    void prestarLibro_debeMarcarComoNoDisponible() throws Exception {
        Libro   libro   = new Libro(10, "Don Q", autorBase, 1605, "ISBN-DQ", true);
        Usuario usuario = new Usuario(5, "Ana", "ana@mail.com", "x");

        when(libroDAO.buscarPorId(10)).thenReturn(Optional.of(libro));
        when(usuarioDAO.buscarPorId(5)).thenReturn(Optional.of(usuario));

        service.prestarLibro(10, 5);

        verify(libroDAO).actualizarDisponibilidad(10, false);
    }

    @Test
    void prestarLibro_debeLanzarExcepcionSiLibroNoDisponible() throws Exception {
        Libro   libro   = new Libro(10, "Don Q", autorBase, 1605, "ISBN-DQ", false);
        Usuario usuario = new Usuario(5, "Ana", "ana@mail.com", "x");

        when(libroDAO.buscarPorId(10)).thenReturn(Optional.of(libro));
        when(usuarioDAO.buscarPorId(5)).thenReturn(Optional.of(usuario));

        assertThrows(LibroNoDisponibleException.class, () -> service.prestarLibro(10, 5));
        verify(libroDAO, never()).actualizarDisponibilidad(anyInt(), anyBoolean());
    }

    @Test
    void prestarLibro_debeLanzarExcepcionSiUsuarioNoExiste() throws Exception {
        when(usuarioDAO.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoRegistradoException.class, () -> service.prestarLibro(1, 99));
    }

    // ----------------------------------------------------------------
    // devolverLibro
    // ----------------------------------------------------------------

    @Test
    void devolverLibro_debeMarcarComoDisponible() throws Exception {
        Libro libro = new Libro(10, "Don Q", autorBase, 1605, "ISBN-DQ", false);
        when(libroDAO.buscarPorId(10)).thenReturn(Optional.of(libro));

        service.devolverLibro(10);

        verify(libroDAO).actualizarDisponibilidad(10, true);
    }

    @Test
    void devolverLibro_debeLanzarExcepcionSiYaEstaDisponible() throws Exception {
        Libro libro = new Libro(10, "Don Q", autorBase, 1605, "ISBN-DQ", true);
        when(libroDAO.buscarPorId(10)).thenReturn(Optional.of(libro));

        assertThrows(IllegalStateException.class, () -> service.devolverLibro(10));
    }

    // ----------------------------------------------------------------
    // buscarPorAutor / librosPublicadosDespuesDe
    // ----------------------------------------------------------------

    @Test
    void buscarPorAutor_debeFiltrarCorrectamente() throws Exception {
        Autor otrAutor = new Autor(2, "Isabel", "Allende", "Bio2");
        List<Libro> todos = List.of(
                new Libro(1, "L1", autorBase, 1967, "I1", true),
                new Libro(2, "L2", otrAutor,  1982, "I2", true)
        );
        when(libroDAO.listar()).thenReturn(todos);

        List<Libro> resultado = service.buscarPorAutor("García");

        assertEquals(1, resultado.size());
        assertEquals("L1", resultado.get(0).getTitulo());
    }

    @Test
    void librosPublicadosDespuesDe_debeFiltrarPorAnio() throws Exception {
        List<Libro> todos = List.of(
                new Libro(1, "Antiguo", autorBase, 1800, "IA", true),
                new Libro(2, "Nuevo",   autorBase, 2020, "IB", true)
        );
        when(libroDAO.listar()).thenReturn(todos);

        List<Libro> resultado = service.librosPublicadosDespuesDe(2000);

        assertEquals(1, resultado.size());
        assertEquals("Nuevo", resultado.get(0).getTitulo());
    }

    // ----------------------------------------------------------------
    // registrarUsuario
    // ----------------------------------------------------------------

    @Test
    void registrarUsuario_debeLanzarDuplicadoSiEmailYaExiste() throws Exception {
        Usuario existente = new Usuario(1, "Existente", "dup@mail.com", "x");
        when(usuarioDAO.buscarPorEmail("dup@mail.com")).thenReturn(Optional.of(existente));

        assertThrows(DuplicadoException.class,
                () -> service.registrarUsuario(new Usuario("Nuevo", "dup@mail.com", "y")));
        verify(usuarioDAO, never()).crear(any());
    }
}
