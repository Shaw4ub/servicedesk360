package sv.edu.itca.servicedesk360.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Seguimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idSeguimiento;
    private int idTicket;
    private String detalle;
    private LocalDateTime fechaRegistro;

    public Seguimiento() {
    }

    public Seguimiento(int idSeguimiento, int idTicket, String detalle,
            LocalDateTime fechaRegistro) {
        this.idSeguimiento = idSeguimiento;
        this.idTicket = idTicket;
        this.detalle = detalle;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(int idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
