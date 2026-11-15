package sv.edu.itca.servicedesk360.dao;

import java.util.List;
import sv.edu.itca.servicedesk360.modelo.Tecnico;

public interface TecnicoDAO {

    List<Tecnico> listarActivos() throws DAOException;
}
