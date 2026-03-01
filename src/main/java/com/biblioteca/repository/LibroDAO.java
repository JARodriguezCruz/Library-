package com.biblioteca.repository;

import com.biblioteca.config.DatabaseConnection;
import com.biblioteca.model.Autor;
import com.biblioteca.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAO implements CrudDAO<Libro, Integer> {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Override
    public void crear(Libro libro) throws SQLException {
        String sql = "INSERT INTO libro (titulo, autor_id, anio, isbn, disponible) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAutor().getId());
            ps.setInt(3, libro.getAnioPublicacion());
            ps.setString(4, libro.getIsbn());
            ps.setBoolean(5, libro.isDisponible());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    libro.setId(keys.getInt(1));
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    @Override
    public List<Libro> listar() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = """
                SELECT l.id, l.titulo, l.anio, l.isbn, l.disponible,
                       a.id AS autor_id, a.nombre, a.apellido, a.biografia
                FROM libro l
                JOIN autor a ON l.autor_id = a.id
                ORDER BY l.titulo
                """;
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearLibro(rs));
            }
        }
        return lista;
    }

    @Override
    public Optional<Libro> buscarPorId(Integer id) throws SQLException {
        String sql = """
                SELECT l.id, l.titulo, l.anio, l.isbn, l.disponible,
                       a.id AS autor_id, a.nombre, a.apellido, a.biografia
                FROM libro l
                JOIN autor a ON l.autor_id = a.id
                WHERE l.id = ?
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLibro(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Libro> buscarPorIsbn(String isbn) throws SQLException {
        String sql = """
                SELECT l.id, l.titulo, l.anio, l.isbn, l.disponible,
                       a.id AS autor_id, a.nombre, a.apellido, a.biografia
                FROM libro l
                JOIN autor a ON l.autor_id = a.id
                WHERE l.isbn = ?
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLibro(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Override
    public void actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE libro SET titulo=?, autor_id=?, anio=?, isbn=?, disponible=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAutor().getId());
            ps.setInt(3, libro.getAnioPublicacion());
            ps.setString(4, libro.getIsbn());
            ps.setBoolean(5, libro.isDisponible());
            ps.setInt(6, libro.getId());
            ps.executeUpdate();
        }
    }

    /** Atajo para marcar un libro como disponible/no disponible. */
    public void actualizarDisponibilidad(int id, boolean disponible) throws SQLException {
        String sql = "UPDATE libro SET disponible = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM libro WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Autor autor = new Autor(
                rs.getInt("autor_id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("biografia")
        );
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                autor,
                rs.getInt("anio"),
                rs.getString("isbn"),
                rs.getBoolean("disponible")
        );
    }
}
