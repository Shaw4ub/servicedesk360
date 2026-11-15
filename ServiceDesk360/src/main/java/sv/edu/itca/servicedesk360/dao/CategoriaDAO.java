package sv.edu.itca.servicedesk360.dao;

import java.util.List;
import sv.edu.itca.servicedesk360.modelo.Categoria;

public interface CategoriaDAO {

    List<Categoria> listarActivas() throws DAOException;
}
