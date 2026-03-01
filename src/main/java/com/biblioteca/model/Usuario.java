package com.biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private int    id;
    private String nombre;
    private String email;
    private String password;

    // Lista de libros prestados actualmente en memoria
    private List<Libro> librosPrestados = new ArrayList<>();

    // ------- Constructores -------

    public Usuario() {}

    public Usuario(String nombre, String email, String password) {
        this.nombre   = nombre;
        this.email    = email;
        this.password = password;
    }

    public Usuario(int id, String nombre, String email, String password) {
        this.id       = id;
        this.nombre   = nombre;
        this.email    = email;
        this.password = password;
    }

    // ------- Getters / Setters -------

    public int getId()                       { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNombre()               { return nombre; }
    public void setNombre(String nombre)    { this.nombre = nombre; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getPassword()             { return password; }
    public void setPassword(String password){ this.password = password; }

    public List<Libro> getLibrosPrestados() { return librosPrestados; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', email='" + email + "'}";
    }
}
