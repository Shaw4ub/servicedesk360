package sv.edu.itca.servicedesk360.persistence;

/**
 * Estructura temporal de lectura usada por las prácticas JDBC.
 *
 * <p>No sustituye al modelo de la aplicación ni al DAO formal de la Guía 6.</p>
 */
public final class ClienteRegistro {

    private final long idCliente;
    private final String nombre;
    private final String correo;
    private final boolean activo;

    public ClienteRegistro(long idCliente, String nombre, String correo,
            boolean activo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.correo = correo;
        this.activo = activo;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return "ClienteRegistro{"
                + "idCliente=" + idCliente
                + ", nombre='" + nombre + '\''
                + ", correo='" + correo + '\''
                + ", activo=" + activo
                + '}';
    }
}
