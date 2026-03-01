package com.biblioteca.repository;

import com.biblioteca.config.DatabaseConnection;
import com.biblioteca.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements CrudDAO<Usuario, Integer> {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Override
    public void crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, email, password) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email, password FROM usuario ORDER BY nombre";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT id, nombre, email, password FROM usuario WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nombre, email, password FROM usuario WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre=?, email=?, password=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.setInt(4, usuario.getId());
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
