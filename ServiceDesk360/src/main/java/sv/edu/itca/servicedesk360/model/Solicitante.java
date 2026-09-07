package sv.edu.itca.servicedesk360.model;

public class Solicitante extends Usuario {

    public Solicitante() {
        super();
        setRol("SOLICITANTE");
    }

    public Solicitante(String nombreCompleto, String correo, String hash) {
        super(nombreCompleto, correo, hash, "SOLICITANTE");
    }
}
