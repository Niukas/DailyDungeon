package niuka.dailydungeon.infraestructura.bd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final Path ARCHIVO_CONFIGURACION = Path.of("db.properties");
    private static final int SEGUNDOS_ESPERA = 2;

    private final String url;
    private final String usuario;
    private final String contrasena;

    // Lee la url, el usuario y la contraseña de la base desde db.properties.
    public ConexionBD() {
        Properties propiedades = leerPropiedades();
        url = propiedades.getProperty("db.url");
        usuario = propiedades.getProperty("db.usuario");
        contrasena = propiedades.getProperty("db.contrasena", "");
    }

    // Cada vez que un repositorio necesita la base, pide una conexión nueva acá.
    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    // Se usa al arrancar la app para saber si MySQL está prendido.
    public boolean hayConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion.isValid(SEGUNDOS_ESPERA);
        } catch (SQLException e) {
            return false;
        }
    }

    private Properties leerPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream entrada = Files.newInputStream(ARCHIVO_CONFIGURACION)) {
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se encontró db.properties. Copiá db.example.properties como db.properties.", e);
        }
        return propiedades;
    }
}
