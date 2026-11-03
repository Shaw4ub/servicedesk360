package sv.edu.itca.servicedesk360.controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.itca.servicedesk360.dao.ClienteDAO;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.impl.ClienteDAOImpl;
import sv.edu.itca.servicedesk360.modelo.Cliente;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private ClienteDAO clienteDAO;

    @Override
    public void init() {
        clienteDAO = new ClienteDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "nuevo":
                    req.setAttribute("cliente", new Cliente());
                    req.getRequestDispatcher(
                            "/WEB-INF/vistas/clientes/formulario.jsp")
                            .forward(req, resp);
                    break;
                case "editar":
                    mostrarEdicion(req, resp);
                    break;
                default:
                    listar(req, resp);
                    break;
            }
        } catch (DAOException ex) {
            throw new ServletException("Error de persistencia", ex);
        } catch (IllegalArgumentException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/clientes");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("clientes", clienteDAO.listar());
        req.getRequestDispatcher("/WEB-INF/vistas/clientes/lista.jsp")
                .forward(req, resp);
    }

    private void mostrarEdicion(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        long id = parseId(req.getParameter("id"));
        Cliente cliente = clienteDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado."));

        req.setAttribute("cliente", cliente);
        req.getRequestDispatcher("/WEB-INF/vistas/clientes/formulario.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");

        try {
            if ("guardar".equals(accion)) {
                guardar(req, resp);
            } else if ("eliminar".equals(accion)) {
                eliminar(req, resp);
            } else if ("estado".equals(accion)) {
                cambiarEstado(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (DAOException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/clientes");
        } catch (IllegalArgumentException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/clientes");
        }
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String idTxt = req.getParameter("idCliente");
        String nombre = limpiar(req.getParameter("nombre"));
        String correo = limpiar(req.getParameter("correo"));
        Long id = (idTxt == null || idTxt.trim().isEmpty())
                ? null
                : Long.valueOf(idTxt.trim());

        if (nombre.length() < 3
                || !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            req.setAttribute("error", "Revise nombre y correo.");
            req.setAttribute("cliente",
                    new Cliente(id, nombre, correo, true));
            req.getRequestDispatcher(
                    "/WEB-INF/vistas/clientes/formulario.jsp")
                    .forward(req, resp);
            return;
        }

        if (clienteDAO.existeCorreo(correo, id)) {
            req.setAttribute("error", "El correo ya está registrado.");
            req.setAttribute("cliente",
                    new Cliente(id, nombre, correo, true));
            req.getRequestDispatcher(
                    "/WEB-INF/vistas/clientes/formulario.jsp")
                    .forward(req, resp);
            return;
        }

        if (id == null) {
            clienteDAO.insertar(new Cliente(null, nombre, correo, true));
        } else {
            Cliente actual = clienteDAO.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Cliente no encontrado."));
            actual.setNombre(nombre);
            actual.setCorreo(correo);

            if (!clienteDAO.actualizar(actual)) {
                throw new IllegalArgumentException(
                        "No fue posible actualizar el cliente.");
            }
        }

        req.getSession().setAttribute("flash",
                "Operación realizada correctamente.");
        resp.sendRedirect(req.getContextPath() + "/clientes");
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        long id = parseId(req.getParameter("id"));

        if (!clienteDAO.eliminar(id)) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        req.getSession().setAttribute("flash", "Cliente eliminado.");
        resp.sendRedirect(req.getContextPath() + "/clientes");
    }

    private void cambiarEstado(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        long id = parseId(req.getParameter("id"));
        boolean activo = Boolean.parseBoolean(req.getParameter("activo"));

        if (!clienteDAO.cambiarEstado(id, activo)) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        req.getSession().setAttribute("flash", "Estado actualizado.");
        resp.sendRedirect(req.getContextPath() + "/clientes");
    }

    private long parseId(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Identificador de cliente inválido.", ex);
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
