package com.biblioteca.service;

import com.biblioteca.exceptions.EntidadNoEncontradaException;
import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorDAO;
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
class AutorServiceTest {

    @Mock private AutorDAO autorDAO;
    private AutorService   service;

    @BeforeEach
    void setUp() {
        service = new AutorService(autorDAO);
    }

    @Test
    void crearAutor_conNombreValido_debeInvocarDAO() throws Exception {
        Autor autor = new Autor("Mario", "Benedetti", "Bio");
        service.crearAutor(autor);
        verify(autorDAO, times(1)).crear(autor);
    }

    @Test
    void crearAutor_sinNombre_debeLanzarIllegalArgument() {
        Autor autor = new Autor("", "Apellido", "Bio");
        assertThrows(IllegalArgumentException.class, () -> service.crearAutor(autor));
        verifyNoInteractions(autorDAO);
    }

    @Test
    void crearAutor_sinApellido_debeLanzarIllegalArgument() {
        Autor autor = new Autor("Nombre", "  ", "Bio");
        assertThrows(IllegalArgumentException.class, () -> service.crearAutor(autor));
    }

    @Test
    void listarAutores_debeRetornarListaDelDAO() throws Exception {
        List<Autor> esperados = List.of(
                new Autor(1, "A", "B", "Bio1"),
                new Autor(2, "C", "D", "Bio2")
        );
        when(autorDAO.listar()).thenReturn(esperados);
        assertEquals(2, service.listarAutores().size());
    }

    @Test
    void buscarPorId_existente_debeRetornarElAutor() throws Exception {
        Autor autor = new Autor(3, "Test", "Autor", "Bio");
        when(autorDAO.buscarPorId(3)).thenReturn(Optional.of(autor));
        Autor resultado = service.buscarPorId(3);
        assertEquals("Test", resultado.getNombre());
    }

    @Test
    void buscarPorId_noExistente_debeLanzarEntidadNoEncontrada() throws Exception {
        when(autorDAO.buscarPorId(999)).thenReturn(Optional.empty());
        assertThrows(EntidadNoEncontradaException.class, () -> service.buscarPorId(999));
    }

    @Test
    void eliminarAutor_existente_debeInvocarDAO() throws Exception {
        when(autorDAO.buscarPorId(1)).thenReturn(Optional.of(new Autor(1, "A", "B", "Bio")));
        service.eliminarAutor(1);
        verify(autorDAO, times(1)).eliminar(1);
    }

    @Test
    void eliminarAutor_noExistente_debeLanzarEntidadNoEncontrada() throws Exception {
        when(autorDAO.buscarPorId(5)).thenReturn(Optional.empty());
        assertThrows(EntidadNoEncontradaException.class, () -> service.eliminarAutor(5));
        verify(autorDAO, never()).eliminar(anyInt());
    }
}
