package sv.edu.itca.servicedesk360.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.itca.servicedesk360.modelo.Categoria;
import sv.edu.itca.servicedesk360.modelo.Cliente;
import sv.edu.itca.servicedesk360.modelo.Equipo;
import sv.edu.itca.servicedesk360.modelo.Seguimiento;
import sv.edu.itca.servicedesk360.modelo.Tecnico;
import sv.edu.itca.servicedesk360.modelo.Ticket;
import sv.edu.itca.servicedesk360.modelo.TicketDetalleDTO;
import sv.edu.itca.servicedesk360.service.ServiceException;
import sv.edu.itca.servicedesk360.service.TicketService;
import sv.edu.itca.servicedesk360.util.Mensajes;

/**
 * Managed Bean de la vista de tickets con PrimeFaces.
 *
 * La vista no contiene SQL ni lógica de persistencia. El Bean coordina la
 * selección de filas, los filtros externos y las acciones del diálogo; las
 * consultas y validaciones de negocio siguen en TicketService/DAO.
 */
@Named("ticketBean")
@ViewScoped
public class TicketBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG
            = Logger.getLogger(TicketBean.class.getName());
    private static final int LIMITE_LISTADO = 500;

    private List<TicketDetalleDTO> tickets;
    private List<Cliente> clientes;
    private List<Categoria> categorias;
    private List<Tecnico> tecnicos;
    private List<Equipo> equiposCliente;
    private List<Seguimiento> seguimientos;

    private Ticket ticket;
    private TicketDetalleDTO seleccionado;
    private String filtroEstado;
    private String filtroPrioridad;
    private String detalleInicial;
    private boolean modoEdicion;

    /*
     * El servicio y los DAO no se serializan con la vista. Se reconstruyen de
     * forma perezosa después de una activación de sesión.
     */
    private transient TicketService ticketService;

    @PostConstruct
    public void init() {
        LOG.info("TicketBean @PostConstruct: inicializando vista PrimeFaces");
        cargarCatalogos();
        prepararNuevo();
        cargar();
    }

    private TicketService service() {
        if (ticketService == null) {
            ticketService = new TicketService();
        }
        return ticketService;
    }

    private void cargarCatalogos() {
        try {
            clientes = service().listarClientesActivos();
            categorias = service().listarCategorias();
            tecnicos = service().listarTecnicos();
            LOG.info("TicketBean.cargarCatalogos: clientes="
                    + clientes.size() + ", categorias=" + categorias.size()
                    + ", tecnicos=" + tecnicos.size());
        } catch (ServiceException ex) {
            Mensajes.error("No fue posible cargar los catálogos: "
                    + ex.getMessage());
        }
    }

    public void cargar() {
        try {
            tickets = service().listarDetalle(
                    filtroEstado, filtroPrioridad, LIMITE_LISTADO, 0);
            LOG.info("TicketBean.cargar: " + tickets.size() + " registros");
        } catch (ServiceException ex) {
            tickets = new ArrayList<>();
            Mensajes.error("No fue posible cargar los tickets: "
                    + ex.getMessage());
        }
    }

    public void aplicarFiltros() {
        cargar();
        if (seleccionado != null && !contieneTicket(seleccionado.getIdTicket())) {
            seleccionado = null;
            seguimientos = new ArrayList<>();
        }
    }

    public void prepararNuevo() {
        LOG.info("TicketBean.prepararNuevo");
        ticket = new Ticket();
        ticket.setPrioridad("MEDIA");
        ticket.setEstado("ABIERTO");
        if (!tecnicos.isEmpty()) {
            ticket.setIdTecnico(tecnicos.get(0).getIdTecnico());
        }
        if (!categorias.isEmpty()) {
            ticket.setIdCategoria(categorias.get(0).getIdCategoria());
        }
        equiposCliente = new ArrayList<>();
        seguimientos = new ArrayList<>();
        seleccionado = null;
        detalleInicial = "";
        modoEdicion = false;
    }

    public void prepararEdicion(TicketDetalleDTO dto) {
        if (dto == null) {
            Mensajes.error("Seleccione un ticket.");
            return;
        }

        LOG.info("TicketBean.prepararEdicion id=" + dto.getIdTicket());
        try {
            ticket = service().buscarTicket(dto.getIdTicket());
            modoEdicion = true;
            detalleInicial = "";
            seleccionado = dto;
            cargarEquipos(ticket.getIdEquipo());
            cargarSeguimientos();
        } catch (ServiceException ex) {
            Mensajes.error(ex.getMessage());
        }
    }

    /**
     * Listener de p:ajax del selector de cliente. Recalcula los equipos y
     * limpia la selección anterior para evitar relaciones incoherentes.
     */
    public void cargarEquipos() {
        cargarEquipos(0);
    }

    private void cargarEquipos(int idEquipoSeleccionado) {
        if (ticket == null || ticket.getIdCliente() <= 0) {
            equiposCliente = new ArrayList<>();
            if (ticket != null) {
                ticket.setIdEquipo(0);
            }
            return;
        }

        try {
            equiposCliente = service()
                    .listarEquiposCliente(ticket.getIdCliente());
            ticket.setIdEquipo(idEquipoSeleccionado);
            LOG.info("TicketBean.cargarEquipos cliente="
                    + ticket.getIdCliente() + ", equipos="
                    + equiposCliente.size());
        } catch (ServiceException ex) {
            equiposCliente = new ArrayList<>();
            ticket.setIdEquipo(0);
            Mensajes.error("No fue posible cargar los equipos: "
                    + ex.getMessage());
        }
    }

    public void guardar() {
        if (ticket == null) {
            Mensajes.error("No hay datos del ticket para guardar.");
            return;
        }

        boolean esNuevo = ticket.getIdTicket() <= 0;
        try {
            if (esNuevo) {
                String detalle = limpiar(detalleInicial);
                if (detalle.isEmpty()) {
                    detalle = ticket.getDescripcion();
                }
                int id = service().registrarTicketConSeguimiento(
                        ticket, detalle);
                ticket.setIdTicket(id);
                Mensajes.info("Ticket registrado correctamente.");
                LOG.info("TicketBean.guardar: creado id=" + id);
            } else {
                service().actualizarTicket(ticket);
                Mensajes.info("Ticket actualizado correctamente.");
                LOG.info("TicketBean.guardar: actualizado id="
                        + ticket.getIdTicket());
            }

            int id = ticket.getIdTicket();
            cargar();
            seleccionado = buscarEnLista(id);
            cargarSeguimientos();
        } catch (ServiceException ex) {
            Mensajes.error(ex.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        } catch (RuntimeException ex) {
            LOG.severe("TicketBean.guardar: " + ex.getMessage());
            Mensajes.error("No fue posible guardar el ticket.");
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void iniciarAtencion(TicketDetalleDTO dto) {
        if (dto == null) {
            Mensajes.error("Seleccione un ticket.");
            return;
        }
        if ("EN_PROCESO".equals(dto.getEstado())) {
            Mensajes.info("El ticket ya está en proceso.");
            return;
        }
        cambiarEstado(dto, "EN_PROCESO",
                "El ticket pasó a EN_PROCESO.",
                "No fue posible iniciar la atención.");
    }

    public void cerrarTicket(TicketDetalleDTO dto) {
        if (dto == null) {
            Mensajes.error("Seleccione un ticket.");
            return;
        }
        if ("CERRADO".equals(dto.getEstado())) {
            Mensajes.info("El ticket ya está cerrado.");
            return;
        }
        cambiarEstado(dto, "CERRADO",
                "El ticket fue cerrado.",
                "No fue posible cerrar el ticket.");
    }

    private void cambiarEstado(TicketDetalleDTO dto, String nuevoEstado,
            String mensajeExito, String mensajeError) {
        try {
            service().cambiarEstado(
                    dto.getIdTicket(), dto.getEstado(), nuevoEstado);
            Mensajes.info(mensajeExito);
            LOG.info("TicketBean.cambiarEstado id=" + dto.getIdTicket()
                    + " estado=" + nuevoEstado);
            int id = dto.getIdTicket();
            cargar();
            seleccionado = buscarEnLista(id);
            cargarSeguimientos();
        } catch (ServiceException ex) {
            Mensajes.error(mensajeError + " " + ex.getMessage());
        }
    }

    /**
     * Listener del evento rowSelect de p:dataTable. Solo coordina la consulta
     * de detalle; el SQL permanece en SeguimientoDAO.
     */
    public void alSeleccionar(SelectEvent event) {
        if (event == null || event.getObject() == null) {
            seleccionado = null;
            seguimientos = new ArrayList<>();
            return;
        }
        seleccionado = (TicketDetalleDTO) event.getObject();
        LOG.info("TicketBean.alSeleccionar id="
                + seleccionado.getIdTicket());
        cargarSeguimientos();
    }

    private void cargarSeguimientos() {
        if (seleccionado == null) {
            seguimientos = new ArrayList<>();
            return;
        }
        try {
            seguimientos = service()
                    .listarSeguimientos(seleccionado.getIdTicket());
        } catch (ServiceException ex) {
            seguimientos = new ArrayList<>();
            Mensajes.error("No fue posible cargar los seguimientos: "
                    + ex.getMessage());
        }
    }

    private boolean contieneTicket(int idTicket) {
        if (tickets == null) {
            return false;
        }
        for (TicketDetalleDTO actual : tickets) {
            if (actual.getIdTicket() == idTicket) {
                return true;
            }
        }
        return false;
    }

    private TicketDetalleDTO buscarEnLista(int idTicket) {
        if (tickets == null) {
            return null;
        }
        for (TicketDetalleDTO actual : tickets) {
            if (actual.getIdTicket() == idTicket) {
                return actual;
            }
        }
        return null;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public String getTituloDialogo() {
        if (ticket != null && modoEdicion) {
            return "Editar ticket #" + ticket.getIdTicket();
        }
        return "Nuevo ticket";
    }

    public List<TicketDetalleDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDetalleDTO> tickets) {
        this.tickets = tickets;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Tecnico> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<Tecnico> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public List<Equipo> getEquiposCliente() {
        return equiposCliente;
    }

    public void setEquiposCliente(List<Equipo> equiposCliente) {
        this.equiposCliente = equiposCliente;
    }

    public List<Seguimiento> getSeguimientos() {
        return seguimientos;
    }

    public void setSeguimientos(List<Seguimiento> seguimientos) {
        this.seguimientos = seguimientos;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public TicketDetalleDTO getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(TicketDetalleDTO seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getFiltroEstado() {
        return filtroEstado;
    }

    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }

    public String getFiltroPrioridad() {
        return filtroPrioridad;
    }

    public void setFiltroPrioridad(String filtroPrioridad) {
        this.filtroPrioridad = filtroPrioridad;
    }

    public String getDetalleInicial() {
        return detalleInicial;
    }

    public void setDetalleInicial(String detalleInicial) {
        this.detalleInicial = detalleInicial;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }
}
