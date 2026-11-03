package sv.edu.itca.servicedesk360.modelo;

public class Cliente {

    private Long idCliente;
    private String nombre;
    private String correo;
    private boolean activo = true;

    public Cliente() {
    }

    public Cliente(Long idCliente, String nombre, String correo,
            boolean activo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.correo = correo;
        this.activo = activo;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
