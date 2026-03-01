package com.biblioteca.repository;

import com.biblioteca.config.DatabaseConnection;
import com.biblioteca.model.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutorDAO implements CrudDAO<Autor, Integer> {

    // La conexión se obtiene del Singleton DatabaseConnection
    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Override
    public void crear(Autor autor) throws SQLException {
        String sql = "INSERT INTO autor (nombre, apellido, biografia) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            ps.setString(3, autor.getBiografia());
            ps.executeUpdate();

            // Recuperar el ID autogenerado y asignarlo al objeto
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    autor.setId(keys.getInt(1));
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    @Override
    public List<Autor> listar() throws SQLException {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, biografia FROM autor ORDER BY apellido, nombre";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearAutor(rs));
            }
        }
        return lista;
    }

    @Override
    public Optional<Autor> buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT id, nombre, apellido, biografia FROM autor WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearAutor(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Override
    public void actualizar(Autor autor) throws SQLException {
        String sql = "UPDATE autor SET nombre = ?, apellido = ?, biografia = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            ps.setString(3, autor.getBiografia());
            ps.setInt(4, autor.getId());
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM autor WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private Autor mapearAutor(ResultSet rs) throws SQLException {
        return new Autor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("biografia")
        );
    }
}
