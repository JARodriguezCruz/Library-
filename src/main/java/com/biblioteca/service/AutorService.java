package com.biblioteca.service;

import com.biblioteca.exceptions.EntidadNoEncontradaException;
import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorDAO;

import java.sql.SQLException;
import java.util.List;

public class AutorService {

    private final AutorDAO autorDAO;

    // Inyección de dependencia: permite pasar un mock en los tests
    public AutorService(AutorDAO autorDAO) {
        this.autorDAO = autorDAO;
    }

    public AutorService() {
        this(new AutorDAO());
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    public void crearAutor(Autor autor) {
        validarCamposRequeridos(autor);
        try {
            autorDAO.crear(autor);
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear autor: " + e.getMessage(), e);
        }
    }

    public List<Autor> listarAutores() {
        try {
            return autorDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar autores: " + e.getMessage(), e);
        }
    }

    public Autor buscarPorId(int id) {
        try {
            return autorDAO.buscarPorId(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Autor con id " + id + " no encontrado."));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar autor: " + e.getMessage(), e);
        }
    }

    public void actualizarAutor(Autor autor) {
        validarCamposRequeridos(autor);
        try {
            autorDAO.buscarPorId(autor.getId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Autor con id " + autor.getId() + " no encontrado."));
            autorDAO.actualizar(autor);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar autor: " + e.getMessage(), e);
        }
    }

    public void eliminarAutor(int id) {
        try {
            autorDAO.buscarPorId(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Autor con id " + id + " no encontrado."));
            autorDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar autor: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // Validaciones
    // ----------------------------------------------------------------

    private void validarCamposRequeridos(Autor autor) {
        if (autor.getNombre() == null || autor.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del autor es obligatorio.");
        }
        if (autor.getApellido() == null || autor.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido del autor es obligatorio.");
        }
    }
}
