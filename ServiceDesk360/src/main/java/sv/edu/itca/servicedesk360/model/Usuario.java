package sv.edu.itca.servicedesk360.model;

import java.io.Serializable;

public abstract class Usuario implements Serializable {

    private String nombreCompleto;
    private String correo;
    private String hash;
    private String rol;

    public Usuario() {
    }

    public Usuario(String nombreCompleto, String correo, String hash, String rol) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.hash = hash;
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
