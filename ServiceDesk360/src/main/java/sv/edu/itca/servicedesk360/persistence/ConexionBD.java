package sv.edu.itca.servicedesk360.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Punto único de apertura de conexiones JDBC.
 *
 * <p>La configuración se mantiene fuera del proyecto, en
 * {@code ~/.servicedesk360/db.properties}, para no incluir credenciales en el
 * código ni en el repositorio.</p>
 */
public final class ConexionBD {

    private static final Logger LOG
            = Logger.getLogger(ConexionBD.class.getName());

    /*
     * En Tomcat, DriverManager puede haberse inicializado antes de que el
     * classloader de la aplicación estuviera disponible. Cargar el driver de
     * forma explícita garantiza que quede registrado aunque el mecanismo
     * automático de servicios no lo detecte.
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE,
                    "No se encontró el controlador JDBC de MySQL en el "
                    + "classpath de la aplicación.", ex);
            throw new IllegalStateException(
                    "No se encontró el controlador JDBC de MySQL.", ex);
        }
    }

    private static final Path ARCHIVO_CONFIGURACION = Path.of(
            System.getProperty("user.home"),
            ".servicedesk360", "db.properties");

    private static final Properties CONFIG = cargarConfiguracion();

    private ConexionBD() {
    }

    private static Properties cargarConfiguracion() {
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(ARCHIVO_CONFIGURACION)) {
            p.load(in);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE,
                    "No se pudo cargar la configuración JDBC externa: "
                    + ARCHIVO_CONFIGURACION, ex);
            throw new IllegalStateException(
                    "No se pudo cargar la configuración JDBC externa.", ex);
        }

        exigirPropiedad(p, "db.url");
        exigirPropiedad(p, "db.user");
        exigirPropiedad(p, "db.password");
        return p;
    }

    private static void exigirPropiedad(Properties p, String clave) {
        if (p.getProperty(clave) == null) {
            throw new IllegalStateException(
                    "Falta la propiedad JDBC '" + clave + "' en "
                    + ARCHIVO_CONFIGURACION + ".");
        }
    }

    /**
     * Abre una conexión nueva. Cada operación debe cerrarla mediante
     * try-with-resources.
     *
     * @return conexión JDBC lista para usarse
     * @throws SQLException si el servidor no está disponible o las
     * credenciales no son válidas
     */
    public static Connection abrir() throws SQLException {
        try {
            return DriverManager.getConnection(
                    CONFIG.getProperty("db.url"),
                    CONFIG.getProperty("db.user"),
                    CONFIG.getProperty("db.password"));
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE,
                    "No se pudo abrir la conexión JDBC con el servidor "
                    + "configurado: " + ex.getMessage());
            throw ex;
        }
    }
}
