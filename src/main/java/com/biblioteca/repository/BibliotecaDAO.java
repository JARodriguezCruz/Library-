package com.biblioteca.repository;

import com.biblioteca.config.DatabaseConnection;
import com.biblioteca.model.Biblioteca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BibliotecaDAO implements CrudDAO<Biblioteca, Integer> {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Override
    public void crear(Biblioteca biblioteca) throws SQLException {
        String sql = "INSERT INTO biblioteca (nombre, direccion, telefono) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, biblioteca.getNombre());
            ps.setString(2, biblioteca.getDireccion());
            ps.setString(3, biblioteca.getTelefono());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    biblioteca.setId(keys.getInt(1));
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    @Override
    public List<Biblioteca> listar() throws SQLException {
        List<Biblioteca> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, direccion, telefono FROM biblioteca ORDER BY nombre";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public Optional<Biblioteca> buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT id, nombre, direccion, telefono FROM biblioteca WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Override
    public void actualizar(Biblioteca biblioteca) throws SQLException {
        String sql = "UPDATE biblioteca SET nombre=?, direccion=?, telefono=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, biblioteca.getNombre());
            ps.setString(2, biblioteca.getDireccion());
            ps.setString(3, biblioteca.getTelefono());
            ps.setInt(4, biblioteca.getId());
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM biblioteca WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private Biblioteca mapear(ResultSet rs) throws SQLException {
        return new Biblioteca(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("direccion"),
                rs.getString("telefono")
        );
    }
}
