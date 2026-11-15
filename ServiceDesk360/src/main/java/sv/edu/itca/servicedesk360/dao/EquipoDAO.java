package sv.edu.itca.servicedesk360.dao;

import java.util.List;
import sv.edu.itca.servicedesk360.modelo.Equipo;

public interface EquipoDAO {

    List<Equipo> listarPorCliente(int idCliente) throws DAOException;

    boolean perteneceACliente(int idEquipo, int idCliente) throws DAOException;
}
