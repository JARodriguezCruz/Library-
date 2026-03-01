package com.biblioteca.service;

import com.biblioteca.exceptions.DuplicadoException;
import com.biblioteca.exceptions.EntidadNoEncontradaException;
import com.biblioteca.exceptions.LibroNoDisponibleException;
import com.biblioteca.exceptions.UsuarioNoRegistradoException;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroDAO;
import com.biblioteca.repository.UsuarioDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BibliotecaService {

    private final LibroDAO   libroDAO;
    private final UsuarioDAO usuarioDAO;

    // Constructor con inyección (para testing con mocks)
    public BibliotecaService(LibroDAO libroDAO, UsuarioDAO usuarioDAO) {
        this.libroDAO   = libroDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public BibliotecaService() {
        this(new LibroDAO(), new UsuarioDAO());
    }

    // ================================================================
    // LIBROS — CRUD
    // ================================================================

    public void agregarLibro(Libro libro) {
        validarLibro(libro);
        try {
            // Verificar ISBN duplicado
            if (libroDAO.buscarPorIsbn(libro.getIsbn()).isPresent()) {
                throw new DuplicadoException("Ya existe un libro con ISBN: " + libro.getIsbn());
            }
            libroDAO.crear(libro);
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar libro.", e);
        }
    }

    public List<Libro> listarLibros() {
        try {
            return libroDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar libros.", e);
        }
    }

    public Libro buscarLibroPorId(int id) {
        try {
            return libroDAO.buscarPorId(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Libro con id " + id + " no encontrado."));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar libro.", e);
        }
    }

    public void actualizarLibro(Libro libro) {
        validarLibro(libro);
        try {
            libroDAO.actualizar(libro);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar libro.", e);
        }
    }

    public void eliminarLibro(int id) {
        try {
            libroDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar libro.", e);
        }
    }

    // ================================================================
    // USUARIOS — CRUD
    // ================================================================

    public void registrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        try {
            if (usuarioDAO.buscarPorEmail(usuario.getEmail()).isPresent()) {
                throw new DuplicadoException("Ya existe un usuario con email: " + usuario.getEmail());
            }
            usuarioDAO.crear(usuario);
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar usuario.", e);
        }
    }

    public List<Usuario> listarUsuarios() {
        try {
            return usuarioDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios.", e);
        }
    }

    public Usuario buscarUsuarioPorId(int id) {
        try {
            return usuarioDAO.buscarPorId(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con id " + id + " no encontrado."));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario.", e);
        }
    }

    public void actualizarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        try {
            usuarioDAO.actualizar(usuario);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario.", e);
        }
    }

    public void eliminarUsuario(int id) {
        try {
            usuarioDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario.", e);
        }
    }

    // ================================================================
    // PRÉSTAMO
    // ================================================================

    public void prestarLibro(int libroId, int usuarioId) {
        try {
            // Validar usuario
            usuarioDAO.buscarPorId(usuarioId)
                    .orElseThrow(() -> new UsuarioNoRegistradoException(
                            "Usuario con id " + usuarioId + " no registrado."));

            // Validar libro
            Libro libro = libroDAO.buscarPorId(libroId)
                    .orElseThrow(() -> new EntidadNoEncontradaException(
                            "Libro con id " + libroId + " no encontrado."));

            if (!libro.isDisponible()) {
                throw new LibroNoDisponibleException(
                        "El libro '" + libro.getTitulo() + "' no está disponible.");
            }

            libroDAO.actualizarDisponibilidad(libroId, false);

        } catch (SQLException e) {
            throw new RuntimeException("Error en préstamo.", e);
        }
    }

    /**
     * Registra la devolución de un libro, marcándolo nuevamente como disponible.
     */
    public void devolverLibro(int libroId) {
        try {
            Libro libro = libroDAO.buscarPorId(libroId)
                    .orElseThrow(() -> new EntidadNoEncontradaException(
                            "Libro con id " + libroId + " no encontrado."));

            if (libro.isDisponible()) {
                throw new IllegalStateException(
                        "El libro '" + libro.getTitulo() + "' ya estaba disponible.");
            }

            libroDAO.actualizarDisponibilidad(libroId, true);

        } catch (SQLException e) {
            throw new RuntimeException("Error en devolución.", e);
        }
    }

    // ================================================================
    // Búsquedas con Streams y Lambdas
    // ================================================================

    /** Retorna libros cuyo autor (nombre o apellido) coincida con el texto buscado. */
    public List<Libro> buscarPorAutor(String nombreAutor) {
        return listarLibros().stream()
                .filter(l -> l.getAutor().getNombreCompleto()
                        .toLowerCase().contains(nombreAutor.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Retorna libros publicados después del año indicado. */
    public List<Libro> librosPublicadosDespuesDe(int anio) {
        return listarLibros().stream()
                .filter(l -> l.getAnioPublicacion() > anio)
                .collect(Collectors.toList());
    }

    /** Retorna solo los libros disponibles para préstamo. */
    public List<Libro> listarLibrosDisponibles() {
        return listarLibros().stream()
                .filter(Libro::isDisponible)
                .collect(Collectors.toList());
    }

    // ================================================================
    // Validaciones internas
    // ================================================================

    private void validarLibro(Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título del libro es obligatorio.");
        }
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN del libro es obligatorio.");
        }
        if (libro.getAutor() == null) {
            throw new IllegalArgumentException("El autor del libro es obligatorio.");
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email del usuario es obligatorio.");
        }
    }
}
