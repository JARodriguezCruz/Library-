package com.biblioteca.service;

import com.biblioteca.exceptions.EntidadNoEncontradaException;
import com.biblioteca.model.Biblioteca;
import com.biblioteca.repository.BibliotecaDAO;

import java.sql.SQLException;
import java.util.List;

public class BibliotecaAdminService {

    private final BibliotecaDAO bibliotecaDAO;

    public BibliotecaAdminService(BibliotecaDAO bibliotecaDAO) {
        this.bibliotecaDAO = bibliotecaDAO;
    }

    public BibliotecaAdminService() {
        this(new BibliotecaDAO());
    }

    public void crearBiblioteca(Biblioteca biblioteca) {
        if (biblioteca.getNombre() == null || biblioteca.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la biblioteca es obligatorio.");
        }
        try {
            bibliotecaDAO.crear(biblioteca);
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear biblioteca.", e);
        }
    }

    public List<Biblioteca> listarBibliotecas() {
        try {
            return bibliotecaDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar bibliotecas.", e);
        }
    }

    public Biblioteca buscarPorId(int id) {
        try {
            return bibliotecaDAO.buscarPorId(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Biblioteca con id " + id + " no encontrada."));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar biblioteca.", e);
        }
    }

    public void actualizarBiblioteca(Biblioteca biblioteca) {
        try {
            bibliotecaDAO.actualizar(biblioteca);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar biblioteca.", e);
        }
    }

    public void eliminarBiblioteca(int id) {
        try {
            bibliotecaDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar biblioteca.", e);
        }
    }
}
