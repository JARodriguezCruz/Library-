package com.biblioteca.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Permite inyectar una URL diferente en las pruebas (H2 en memoria)
    private static String overrideUrl;
    private static String overrideUser;
    private static String overridePassword;

    // Constructor privado: nadie puede instanciar esta clase desde afuera
    private DatabaseConnection() {
        try {
            if (overrideUrl != null) {
                // Modo pruebas: usa los parámetros inyectados
                connection = DriverManager.getConnection(overrideUrl, overrideUser, overridePassword);
            } else {
                // Modo producción: lee db.properties
                Properties props = loadProperties();
                connection = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.user"),
                        props.getProperty("db.password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al establecer conexión con la base de datos.", e);
        }
    }

    /**
     * Punto de acceso global al Singleton.
     * Usa inicialización lazy (crea la instancia la primera vez que se solicita).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Permite a los tests inyectar una URL de base de datos diferente (H2).
     * Debe llamarse ANTES del primer getInstance().
     */
    public static void configureForTesting(String url, String user, String password) {
        overrideUrl      = url;
        overrideUser     = user;
        overridePassword = password;
        instance         = null; // fuerza recreación
    }

    /** Restablece la configuración original (usado al finalizar tests). */
    public static void resetInstance() {
        instance         = null;
        overrideUrl      = null;
        overrideUser     = null;
        overridePassword = null;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = null;
                return getInstance().connection;
            }
        } catch (SQLException ignored) {}
        return connection;
    }

    // ----------------------------------------------------------------
    // Métodos de soporte
    // ----------------------------------------------------------------

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("No se encontró db.properties en el classpath.");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer db.properties.", e);
        }
        return props;
    }
}
