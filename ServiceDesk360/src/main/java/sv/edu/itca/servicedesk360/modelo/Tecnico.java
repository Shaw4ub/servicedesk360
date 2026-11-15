package sv.edu.itca.servicedesk360.modelo;

public class Tecnico {

    private int idTecnico;
    private String nombre;

    public Tecnico() {
    }

    public Tecnico(int idTecnico, String nombre) {
        this.idTecnico = idTecnico;
        this.nombre = nombre;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
