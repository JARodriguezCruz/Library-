package com.biblioteca;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Biblioteca;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.AutorService;
import com.biblioteca.service.BibliotecaAdminService;
import com.biblioteca.service.BibliotecaService;

import java.util.List;

public class App {

    public static void main(String[] args) {

        AutorService autorService   = new AutorService();
        BibliotecaService     service        = new BibliotecaService();
        BibliotecaAdminService adminService  = new BibliotecaAdminService();

        System.out.println("=== Sistema de Biblioteca ===\n");

        // 1. Crear una sede
        Biblioteca sede = new Biblioteca("Biblioteca Central", "Av. Principal 100", "555-1234");
        adminService.crearBiblioteca(sede);
        System.out.println("Sede creada: " + sede);

        // 2. Crear un autor
        Autor autor = new Autor("Gabriel", "García Márquez", "Premio Nobel de Literatura 1982");
        autorService.crearAutor(autor);
        System.out.println("Autor creado: " + autor);

        // 3. Agregar un libro
        Libro libro = new Libro("Cien años de soledad", autor, 1967, "ISBN-CGM-001");
        service.agregarLibro(libro);
        System.out.println("Libro agregado: " + libro);

        // 4. Registrar un usuario
        Usuario usuario = new Usuario("Ana López", "ana@mail.com", "segura123");
        service.registrarUsuario(usuario);
        System.out.println("Usuario registrado: " + usuario);

        // 5. Listar libros disponibles
        System.out.println("\n--- Libros disponibles ---");
        List<Libro> disponibles = service.listarLibrosDisponibles();
        disponibles.forEach(l -> System.out.println("  " + l));

        // 6. Realizar préstamo
        service.prestarLibro(libro.getId(), usuario.getId());
        System.out.println("\nPréstamo realizado: libro '" + libro.getTitulo()
                + "' asignado a '" + usuario.getNombre() + "'");

        // 7. Verificar que ya no está disponible
        Libro libroActualizado = service.buscarLibroPorId(libro.getId());
        System.out.println("¿Disponible tras préstamo? " + libroActualizado.isDisponible());

        // 8. Devolver el libro
        service.devolverLibro(libro.getId());
        System.out.println("Libro devuelto correctamente.");

        // 9. Listar todos los autores
        System.out.println("\n--- Autores registrados ---");
        autorService.listarAutores().forEach(a -> System.out.println("  " + a));
    }
}