package com.biblioteca;

import com.biblioteca.config.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Clase base para tests de integración con H2 en memoria.
 * Configura el Singleton DatabaseConnection para que apunte a H2
 * y crea el esquema equivalente al de MySQL.
 */
public abstract class H2TestBase {

    protected static Connection conn;

    @BeforeAll
    static void configurarH2() throws Exception {
        DatabaseConnection.configureForTesting(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
                "sa", ""
        );
        conn = DatabaseConnection.getInstance().getConnection();
        crearEsquema(conn);
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (conn != null) {
            try (Statement st = conn.createStatement()) {
                st.execute("DROP ALL OBJECTS");
            }
        }
        DatabaseConnection.resetInstance();
    }

    private static void crearEsquema(Connection c) throws Exception {
        try (Statement st = c.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS autor (
                    id         INT AUTO_INCREMENT PRIMARY KEY,
                    nombre     VARCHAR(100) NOT NULL,
                    apellido   VARCHAR(100) NOT NULL,
                    biografia  TEXT
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
                    isbn       VARCHAR(30) NOT NULL UNIQUE,
                    disponible BOOLEAN NOT NULL DEFAULT TRUE,
                    FOREIGN KEY (autor_id) REFERENCES autor(id)
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
        }
    }
}
