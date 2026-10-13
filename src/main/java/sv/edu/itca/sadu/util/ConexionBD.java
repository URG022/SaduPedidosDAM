package sv.edu.itca.sadu.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Unico punto de conexion a MySQL para todo el equipo.
 * Lee la configuracion de (en este orden):
 *   1. ~/.sadu/db.properties   (carpeta personal del usuario)
 *   2. src/main/resources/db.properties (no se sube a GitHub)
 */
public final class ConexionBD {

    private static Properties config;

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        Properties p = cargarConfiguracion();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver de MySQL. Ejecute Clean and Build.", e);
        }
        return DriverManager.getConnection(
                p.getProperty("db.url"), p.getProperty("db.user"), p.getProperty("db.password"));
    }

    private static synchronized Properties cargarConfiguracion() throws SQLException {
        if (config != null) {
            return config;
        }
        Properties p = new Properties();
        Path externo = Paths.get(System.getProperty("user.home"), ".sadu", "db.properties");
        try {
            if (Files.exists(externo)) {
                try (InputStream in = Files.newInputStream(externo)) {
                    p.load(in);
                }
            } else {
                try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (in == null) {
                        throw new SQLException("Falta db.properties. Copie src/main/resources/db.properties.example "
                                + "como db.properties y ponga su clave de MySQL.");
                    }
                    p.load(in);
                }
            }
        } catch (IOException e) {
            throw new SQLException("No se pudo leer db.properties", e);
        }
        config = p;
        return config;
    }
}
