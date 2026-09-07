package sv.edu.itca.servicedesk360.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TicketSoporte implements Serializable {

    private long id;
    private String titulo;
    private String descripcion;
    private Solicitante solicitante;
    private PrioridadTicket prioridad;
    private EstadoTicket estado;
    private LocalDateTime fechaCreacion;

    public TicketSoporte() {
        this.estado = EstadoTicket.ABIERTO;
        this.fechaCreacion = LocalDateTime.now();
    }

    public TicketSoporte(long id, String titulo, String descripcion, Solicitante solicitante, PrioridadTicket prioridad) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.prioridad = prioridad;
        this.estado = EstadoTicket.ABIERTO;
        this.fechaCreacion = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }

    public PrioridadTicket getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadTicket prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void setEstado(EstadoTicket estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
