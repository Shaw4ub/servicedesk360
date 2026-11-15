package sv.edu.itca.servicedesk360.dao;

import java.sql.Connection;
import java.util.List;
import sv.edu.itca.servicedesk360.modelo.Seguimiento;

public interface SeguimientoDAO {

    int registrar(Connection cn, Seguimiento seguimiento) throws DAOException;

    List<Seguimiento> listarPorTicket(int idTicket) throws DAOException;
}
