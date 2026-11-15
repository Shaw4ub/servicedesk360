package sv.edu.itca.servicedesk360.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import sv.edu.itca.servicedesk360.modelo.Ticket;
import sv.edu.itca.servicedesk360.modelo.TicketDetalleDTO;

public interface TicketDAO {

    int registrar(Connection cn, Ticket ticket) throws DAOException;

    List<TicketDetalleDTO> listarDetalle(String estado, String prioridad,
            int limite, int offset) throws DAOException;

    int contar(String estado, String prioridad) throws DAOException;

    Optional<TicketDetalleDTO> buscarDetalle(int idTicket)
            throws DAOException;

    boolean actualizarEstado(int idTicket, String estadoEsperado,
            String nuevoEstado) throws DAOException;
}
