package sv.edu.itca.servicedesk360.model;

public class Tecnico extends Usuario {

    public Tecnico() {
        super();
        setRol("TECNICO");
    }

    public Tecnico(String nombreCompleto, String correo, String hash) {
        super(nombreCompleto, correo, hash, "TECNICO");
    }
}
