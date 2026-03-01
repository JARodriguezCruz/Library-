package com.biblioteca.model;

public class Libro {

    private int    id;
    private String titulo;
    private Autor  autor;
    private int    anioPublicacion;
    private String isbn;
    private boolean disponible;

    // ------- Constructores -------

    public Libro() {}

    /** Constructor para crear un libro nuevo (sin ID, disponible por defecto). */
    public Libro(String titulo, Autor autor, int anioPublicacion, String isbn) {
        this.titulo          = titulo;
        this.autor           = autor;
        this.anioPublicacion = anioPublicacion;
        this.isbn            = isbn;
        this.disponible      = true;
    }

    /** Constructor completo (reconstrucción desde la BD). */
    public Libro(int id, String titulo, Autor autor, int anioPublicacion,
                 String isbn, boolean disponible) {
        this.id              = id;
        this.titulo          = titulo;
        this.autor           = autor;
        this.anioPublicacion = anioPublicacion;
        this.isbn            = isbn;
        this.disponible      = disponible;
    }

    // ------- Getters / Setters -------

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getTitulo()                   { return titulo; }
    public void setTitulo(String titulo)        { this.titulo = titulo; }

    public Autor getAutor()                     { return autor; }
    public void setAutor(Autor autor)           { this.autor = autor; }

    public int getAnioPublicacion()                        { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion)    { this.anioPublicacion = anioPublicacion; }

    public String getIsbn()                     { return isbn; }
    public void setIsbn(String isbn)            { this.isbn = isbn; }

    public boolean isDisponible()               { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", titulo='" + titulo + "', isbn='" + isbn +
               "', disponible=" + disponible + "}";
    }
}
