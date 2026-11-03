package sv.edu.itca.servicedesk360.persistence;

import java.time.LocalDateTime;

/**
 * Fila de solo lectura para comprobar el listado de tickets con JOIN.
 *
 * <p>Es una estructura temporal de laboratorio; en la Guía 6 se reemplazará
 * por el DAO y el modelo definitivos.</p>
 */
public final class TicketVista {

    private final long idTicket;
    private final String titulo;
    private final String prioridad;
    private final String estado;
    private final LocalDateTime fechaCreacion;
    private final String cliente;
    private final String categoria;

    public TicketVista(long idTicket, String titulo, String prioridad,
            String estado, LocalDateTime fechaCreacion, String cliente,
            String categoria) {
        this.idTicket = idTicket;
        this.titulo = titulo;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.cliente = cliente;
        this.categoria = categoria;
    }

    public long getIdTicket() {
        return idTicket;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getCliente() {
        return cliente;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return "TicketVista{"
                + "idTicket=" + idTicket
                + ", titulo='" + titulo + '\''
                + ", prioridad='" + prioridad + '\''
                + ", estado='" + estado + '\''
                + ", fechaCreacion=" + fechaCreacion
                + ", cliente='" + cliente + '\''
                + ", categoria='" + categoria + '\''
                + '}';
    }
}
