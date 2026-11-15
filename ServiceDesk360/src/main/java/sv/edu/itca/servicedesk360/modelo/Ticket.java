package sv.edu.itca.servicedesk360.modelo;

import java.time.LocalDateTime;

public class Ticket {

    private int idTicket;
    private int idCliente;
    private int idEquipo;
    private int idTecnico;
    private int idCategoria;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private String estado;
    private LocalDateTime fechaCreacion;

    public Ticket() {
    }

    public Ticket(int idTicket, int idCliente, int idEquipo, int idTecnico,
            int idCategoria, String titulo, String descripcion,
            String prioridad, String estado, LocalDateTime fechaCreacion) {
        this.idTicket = idTicket;
        this.idCliente = idCliente;
        this.idEquipo = idEquipo;
        this.idTecnico = idTecnico;
        this.idCategoria = idCategoria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
