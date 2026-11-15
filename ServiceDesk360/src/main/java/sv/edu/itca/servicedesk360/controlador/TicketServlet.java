package sv.edu.itca.servicedesk360.controlador;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.itca.servicedesk360.dao.CategoriaDAO;
import sv.edu.itca.servicedesk360.dao.ClienteDAO;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.EquipoDAO;
import sv.edu.itca.servicedesk360.dao.TecnicoDAO;
import sv.edu.itca.servicedesk360.dao.impl.CategoriaDAOImpl;
import sv.edu.itca.servicedesk360.dao.impl.ClienteDAOImpl;
import sv.edu.itca.servicedesk360.dao.impl.EquipoDAOImpl;
import sv.edu.itca.servicedesk360.dao.impl.TecnicoDAOImpl;
import sv.edu.itca.servicedesk360.modelo.Equipo;
import sv.edu.itca.servicedesk360.modelo.Ticket;
import sv.edu.itca.servicedesk360.modelo.TicketDetalleDTO;
import sv.edu.itca.servicedesk360.service.ServiceException;
import sv.edu.itca.servicedesk360.service.TicketService;

@WebServlet(urlPatterns = {"/tickets", "/tickets/nuevo"})
public class TicketServlet extends HttpServlet {

    private static final List<String> ESTADOS
            = Arrays.asList("ABIERTO", "ASIGNADO", "EN_PROCESO", "CERRADO");
    private static final List<String> PRIORIDADES
            = Arrays.asList("BAJA", "MEDIA", "ALTA");
    private static final int LIMITE_POR_PAGINA = 10;

    private final TicketService ticketService = new TicketService();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final TecnicoDAO tecnicoDAO = new TecnicoDAOImpl();
    private final EquipoDAO equipoDAO = new EquipoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            accion = req.getParameter("action");
        }
        if (accion == null || accion.trim().isEmpty()) {
            accion = "/tickets/nuevo".equals(req.getServletPath())
                    ? "nuevo"
                    : "listar";
        }

        try {
            switch (accion) {
                case "nuevo":
                    mostrarFormulario(req, resp);
                    break;
                case "ver":
                    ver(req, resp);
                    break;
                case "equipos":
                    responderEquipos(req, resp);
                    break;
                default:
                    listar(req, resp);
                    break;
            }
        } catch (ServiceException ex) {
            req.setAttribute("mensajeError", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp")
                    .forward(req, resp);
        } catch (DAOException ex) {
            throw new ServletException("Error de persistencia", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            accion = req.getParameter("action");
        }

        if ("guardar".equals(accion)) {
            guardar(req, resp);
        } else if ("cambiarEstado".equals(accion)) {
            cambiarEstado(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String estado = normalizarFiltro(
                req.getParameter("estado"), ESTADOS);
        String prioridad = normalizarFiltro(
                req.getParameter("prioridad"), PRIORIDADES);
        int pagina = parsePagina(req.getParameter("pagina"));
        int offset = (pagina - 1) * LIMITE_POR_PAGINA;

        List<TicketDetalleDTO> tickets = ticketService.listarDetalle(
                estado, prioridad, LIMITE_POR_PAGINA, offset);
        int total = ticketService.contar(estado, prioridad);
        int totalPaginas = (int) Math.ceil(
                total / (double) LIMITE_POR_PAGINA);

        req.setAttribute("tickets", tickets);
        req.setAttribute("pagina", pagina);
        req.setAttribute("total", total);
        req.setAttribute("totalPaginas", totalPaginas);
        req.setAttribute("estado", estado);
        req.setAttribute("prioridad", prioridad);

        if ("creado".equals(req.getParameter("ok"))) {
            req.setAttribute("mensajeExito",
                    "Ticket registrado correctamente.");
            req.setAttribute("idCreado", req.getParameter("id"));
        }

        req.getRequestDispatcher("/WEB-INF/vistas/tickets/lista.jsp")
                .forward(req, resp);
    }

    private void mostrarFormulario(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        cargarCatalogos(req);

        String idCliente = req.getParameter("idCliente");
        if (idCliente != null && !idCliente.trim().isEmpty()) {
            int id = parseEntero(idCliente);
            if (id > 0) {
                req.setAttribute("equipos", equipoDAO.listarPorCliente(id));
            }
        }

        req.getRequestDispatcher("/WEB-INF/vistas/tickets/formulario.jsp")
                .forward(req, resp);
    }

    private void ver(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseEntero(req.getParameter("id"));

        if (id <= 0) {
            req.getSession().setAttribute("flashError",
                    "Identificador de ticket inválido.");
            resp.sendRedirect(req.getContextPath() + "/tickets");
            return;
        }

        try {
            TicketDetalleDTO ticket = ticketService.buscarDetalle(id);
            req.setAttribute("ticket", ticket);
            req.setAttribute("seguimientos",
                    ticketService.listarSeguimientos(id));
            req.getRequestDispatcher("/WEB-INF/vistas/tickets/detalle.jsp")
                    .forward(req, resp);
        } catch (ServiceException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/tickets");
        }
    }

    private void responderEquipos(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        int idCliente = parseEntero(req.getParameter("idCliente"));
        List<Equipo> equipos = idCliente > 0
                ? equipoDAO.listarPorCliente(idCliente)
                : Collections.emptyList();

        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < equipos.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            Equipo equipo = equipos.get(i);
            json.append("{\"idEquipo\":").append(equipo.getIdEquipo())
                    .append(",\"descripcion\":\"")
                    .append(escaparJson(equipo.getDescripcion()))
                    .append("\"}");
        }
        json.append(']');
        resp.getWriter().write(json.toString());
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        Ticket ticket = new Ticket();
        ticket.setIdCliente(parseEntero(req.getParameter("idCliente")));
        ticket.setIdEquipo(parseEntero(req.getParameter("idEquipo")));
        ticket.setIdTecnico(parseEntero(req.getParameter("idTecnico")));
        ticket.setIdCategoria(parseEntero(req.getParameter("idCategoria")));
        ticket.setTitulo(req.getParameter("titulo"));
        ticket.setDescripcion(req.getParameter("descripcion"));
        ticket.setPrioridad(req.getParameter("prioridad"));
        String detalleInicial = req.getParameter("detalleInicial");

        try {
            int id = ticketService.registrarTicketConSeguimiento(
                    ticket, detalleInicial);
            resp.sendRedirect(req.getContextPath()
                    + "/tickets?ok=creado&id=" + id);
        } catch (ServiceException ex) {
            req.setAttribute("error", ex.getMessage());
            cargarFormularioConError(req, ticket.getIdCliente());
            req.getRequestDispatcher(
                    "/WEB-INF/vistas/tickets/formulario.jsp")
                    .forward(req, resp);
        }
    }

    private void cambiarEstado(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        int idTicket = parseEntero(req.getParameter("idTicket"));

        if (idTicket <= 0) {
            req.getSession().setAttribute("flashError",
                    "Identificador de ticket inválido.");
            resp.sendRedirect(req.getContextPath() + "/tickets");
            return;
        }

        try {
            ticketService.cambiarEstado(
                    idTicket,
                    req.getParameter("estadoEsperado"),
                    req.getParameter("nuevoEstado"));
            req.getSession().setAttribute("flash",
                    "Estado actualizado correctamente.");
        } catch (ServiceException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
        }

        resp.sendRedirect(req.getContextPath()
                + "/tickets?accion=ver&id=" + idTicket);
    }

    private void cargarCatalogos(HttpServletRequest req) {
        req.setAttribute("clientes", clienteDAO.listarActivos());
        req.setAttribute("categorias", categoriaDAO.listarActivas());
        req.setAttribute("tecnicos", tecnicoDAO.listarActivos());
    }

    private void cargarFormularioConError(HttpServletRequest req,
            int idCliente) {
        try {
            cargarCatalogos(req);
            if (idCliente > 0) {
                req.setAttribute("equipos",
                        equipoDAO.listarPorCliente(idCliente));
            }
        } catch (DAOException ex) {
            req.setAttribute("error",
                    "No fue posible cargar los catálogos del formulario.");
        }
    }

    private String normalizarFiltro(String valor, List<String> permitidos) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String limpio = valor.trim().toUpperCase();
        return permitidos.contains(limpio) ? limpio : null;
    }

    private int parsePagina(String valor) {
        try {
            int pagina = Integer.parseInt(valor);
            return pagina < 1 ? 1 : pagina;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private int parseEntero(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
