package sv.edu.itca.servicedesk360.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.EquipoDAO;
import sv.edu.itca.servicedesk360.dao.SeguimientoDAO;
import sv.edu.itca.servicedesk360.dao.TicketDAO;
import sv.edu.itca.servicedesk360.dao.impl.EquipoDAOImpl;
import sv.edu.itca.servicedesk360.dao.impl.SeguimientoDAOImpl;
import sv.edu.itca.servicedesk360.dao.impl.TicketDAOImpl;
import sv.edu.itca.servicedesk360.modelo.Seguimiento;
import sv.edu.itca.servicedesk360.modelo.Ticket;
import sv.edu.itca.servicedesk360.modelo.TicketDetalleDTO;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class TicketService {

    private static final Set<String> PRIORIDADES
            = Set.of("BAJA", "MEDIA", "ALTA");
    private static final Set<String> ESTADOS
            = Set.of("ABIERTO", "ASIGNADO", "EN_PROCESO", "CERRADO");

    private final TicketDAO ticketDAO = new TicketDAOImpl();
    private final SeguimientoDAO seguimientoDAO = new SeguimientoDAOImpl();
    private final EquipoDAO equipoDAO = new EquipoDAOImpl();

    public int registrarTicketConSeguimiento(Ticket ticket,
            String detalleInicial) throws ServiceException {
        validar(ticket, detalleInicial);

        try {
            if (!equipoDAO.perteneceACliente(
                    ticket.getIdEquipo(), ticket.getIdCliente())) {
                throw new ServiceException(
                        "El equipo seleccionado no pertenece al cliente.");
            }
        } catch (DAOException ex) {
            throw new ServiceException(
                    "No fue posible validar el equipo.", ex);
        }

        try (Connection cn = ConexionBD.abrir()) {
            cn.setAutoCommit(false);
            try {
                int idTicket = ticketDAO.registrar(cn, ticket);

                Seguimiento seguimiento = new Seguimiento();
                seguimiento.setIdTicket(idTicket);
                seguimiento.setDetalle(detalleInicial);

                if (seguimientoDAO.registrar(cn, seguimiento) != 1) {
                    throw new ServiceException(
                            "No se registró el seguimiento inicial.");
                }

                cn.commit();
                return idTicket;
            } catch (Exception ex) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                if (ex instanceof ServiceException) {
                    throw (ServiceException) ex;
                }
                throw new ServiceException(
                        "La operación fue revertida.", ex);
            } finally {
                try {
                    cn.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // La conexión se cierra de todos modos.
                }
            }
        } catch (SQLException ex) {
            throw new ServiceException(
                    "No fue posible completar el proceso.", ex);
        }
    }

    public List<TicketDetalleDTO> listarDetalle(String estado,
            String prioridad, int limite, int offset)
            throws ServiceException {
        try {
            return ticketDAO.listarDetalle(
                    estado, prioridad, limite, offset);
        } catch (DAOException ex) {
            throw new ServiceException(
                    "No fue posible listar los tickets.", ex);
        }
    }

    public int contar(String estado, String prioridad)
            throws ServiceException {
        try {
            return ticketDAO.contar(estado, prioridad);
        } catch (DAOException ex) {
            throw new ServiceException(
                    "No fue posible contar los tickets.", ex);
        }
    }

    public TicketDetalleDTO buscarDetalle(int idTicket)
            throws ServiceException {
        try {
            return ticketDAO.buscarDetalle(idTicket)
                    .orElseThrow(() -> new ServiceException(
                            "Ticket no encontrado."));
        } catch (DAOException ex) {
            throw new ServiceException(
                    "No fue posible consultar el ticket.", ex);
        }
    }

    public List<Seguimiento> listarSeguimientos(int idTicket)
            throws ServiceException {
        try {
            return seguimientoDAO.listarPorTicket(idTicket);
        } catch (DAOException ex) {
            throw new ServiceException(
                    "No fue posible listar los seguimientos.", ex);
        }
    }

    public void cambiarEstado(int idTicket, String estadoEsperado,
            String nuevoEstado) throws ServiceException {
        if (estadoEsperado == null || estadoEsperado.trim().isEmpty()) {
            throw new ServiceException(
                    "El estado esperado es obligatorio.");
        }
        if (nuevoEstado == null || !ESTADOS.contains(nuevoEstado)) {
            throw new ServiceException("Estado no válido.");
        }
        if (estadoEsperado.equals(nuevoEstado)) {
            return;
        }

        try {
            if (!ticketDAO.actualizarEstado(
                    idTicket, estadoEsperado, nuevoEstado)) {
                throw new ServiceException(
                        "El ticket fue modificado por otra operación. "
                        + "Recargue la pantalla e intente nuevamente.");
            }
        } catch (DAOException ex) {
            throw new ServiceException(
                    "No fue posible actualizar el estado.", ex);
        }
    }

    private void validar(Ticket ticket, String detalle)
            throws ServiceException {
        if (ticket == null) {
            throw new ServiceException("El ticket es obligatorio.");
        }
        if (ticket.getIdCliente() <= 0) {
            throw new ServiceException("Cliente requerido.");
        }
        if (ticket.getIdEquipo() <= 0) {
            throw new ServiceException("Equipo requerido.");
        }
        if (ticket.getIdTecnico() <= 0) {
            throw new ServiceException("Técnico requerido.");
        }
        if (ticket.getIdCategoria() <= 0) {
            throw new ServiceException("Categoría requerida.");
        }

        String titulo = ticket.getTitulo() == null
                ? ""
                : ticket.getTitulo().trim();
        if (titulo.length() < 5) {
            throw new ServiceException(
                    "El título debe contener al menos 5 caracteres.");
        }
        ticket.setTitulo(titulo);

        String descripcion = ticket.getDescripcion() == null
                ? ""
                : ticket.getDescripcion().trim();
        if (descripcion.isEmpty()) {
            throw new ServiceException("La descripción es obligatoria.");
        }
        ticket.setDescripcion(descripcion);

        String prioridad = ticket.getPrioridad() == null
                ? null
                : ticket.getPrioridad().trim().toUpperCase();
        if (prioridad == null || !PRIORIDADES.contains(prioridad)) {
            throw new ServiceException("Prioridad no válida.");
        }
        ticket.setPrioridad(prioridad);

        if (detalle == null || detalle.trim().isEmpty()) {
            throw new ServiceException(
                    "Seguimiento inicial requerido.");
        }
    }
}
