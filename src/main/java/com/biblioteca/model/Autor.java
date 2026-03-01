package com.biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Autor {

    private int id;
    private String nombre;
    private String apellido;
    private String biografia;

    // Lista en memoria, útil para operaciones de dominio sin acceder a la BD
    private List<Libro> libros = new ArrayList<>();

    // ------- Constructores -------

    public Autor() {}

    public Autor(String nombre, String apellido, String biografia) {
        this.nombre    = nombre;
        this.apellido  = apellido;
        this.biografia = biografia;
    }

    public Autor(int id, String nombre, String apellido, String biografia) {
        this.id        = id;
        this.nombre    = nombre;
        this.apellido  = apellido;
        this.biografia = biografia;
    }

    // ------- Getters / Setters -------

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getNombre()             { return nombre; }
    public void setNombre(String nombre)  { this.nombre = nombre; }

    public String getApellido()                    { return apellido; }
    public void setApellido(String apellido)       { this.apellido = apellido; }

    public String getBiografia()                   { return biografia; }
    public void setBiografia(String biografia)     { this.biografia = biografia; }

    public List<Libro> getLibros()                 { return libros; }

    // ------- Métodos de dominio -------

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public void agregarLibro(Libro libro) {
        if (!libros.contains(libro)) {
            libros.add(libro);
        }
    }

    @Override
    public String toString() {
        return "Autor{id=" + id + ", nombre='" + getNombreCompleto() + "'}";
    }
}
