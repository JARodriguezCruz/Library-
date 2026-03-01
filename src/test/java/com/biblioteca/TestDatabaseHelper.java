package com.biblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabaseHelper {

    private static final String H2_URL  = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, H2_USER, H2_PASS);
    }

    public static void crearEsquema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS autor (
                        id        INT AUTO_INCREMENT PRIMARY KEY,
                        nombre    VARCHAR(100) NOT NULL,
                        apellido  VARCHAR(100) NOT NULL,
                        biografia TEXT
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS usuario (
                        id       INT AUTO_INCREMENT PRIMARY KEY,
                        nombre   VARCHAR(150) NOT NULL,
                        email    VARCHAR(200) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS biblioteca (
                        id        INT AUTO_INCREMENT PRIMARY KEY,
                        nombre    VARCHAR(200) NOT NULL,
                        direccion VARCHAR(300),
                        telefono  VARCHAR(20)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS libro (
                        id         INT AUTO_INCREMENT PRIMARY KEY,
                        titulo     VARCHAR(300) NOT NULL,
                        autor_id   INT NOT NULL,
                        anio       INT,
                        isbn       VARCHAR(50) NOT NULL UNIQUE,
                        disponible BOOLEAN NOT NULL DEFAULT TRUE,
                        CONSTRAINT fk_libro_autor FOREIGN KEY (autor_id) REFERENCES autor(id)
                    )
                    """);
        }
    }

    public static void limpiarTablas(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("SET REFERENTIAL_INTEGRITY FALSE");
            st.execute("TRUNCATE TABLE libro");
            st.execute("TRUNCATE TABLE autor");
            st.execute("TRUNCATE TABLE usuario");
            st.execute("TRUNCATE TABLE biblioteca");
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }
}
