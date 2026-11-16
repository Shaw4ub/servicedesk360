package sv.edu.itca.servicedesk360.bean;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.TicketDAO;
import sv.edu.itca.servicedesk360.dao.impl.TicketDAOImpl;
import sv.edu.itca.servicedesk360.modelo.TicketDetalleDTO;
import sv.edu.itca.servicedesk360.util.Mensajes;

/**
 * Vista JSF de solo lectura para reutilizar la consulta JOIN de la Guía 7.
 */
@Named("ticketBean")
@ViewScoped
public class TicketBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG
            = Logger.getLogger(TicketBean.class.getName());

    private List<TicketDetalleDTO> tickets;
    private transient TicketDAO ticketDAO;

    @PostConstruct
    public void init() {
        LOG.info("TicketBean @PostConstruct: inicializando vista de tickets");
        ticketDAO = new TicketDAOImpl();
        cargar();
    }

    private TicketDAO dao() {
        if (ticketDAO == null) {
            ticketDAO = new TicketDAOImpl();
        }
        return ticketDAO;
    }

    public void cargar() {
        try {
            tickets = dao().listarDetalle(null, null, 20, 0);
            LOG.info("TicketBean.cargar: " + tickets.size() + " registros");
        } catch (DAOException ex) {
            Mensajes.error("No fue posible cargar los tickets.");
        }
    }

    public List<TicketDetalleDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDetalleDTO> tickets) {
        this.tickets = tickets;
    }
}
