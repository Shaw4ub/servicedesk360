package sv.edu.itca.servicedesk360.bean;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sv.edu.itca.servicedesk360.dao.ClienteDAO;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.impl.ClienteDAOImpl;
import sv.edu.itca.servicedesk360.modelo.Cliente;
import sv.edu.itca.servicedesk360.util.Mensajes;

/**
 * Managed Bean de vista para el CRUD de clientes con JSF.
 *
 * La vista solo enlaza propiedades y acciones. Las consultas SQL permanecen
 * encapsuladas en ClienteDAOImpl.
 */
@Named("clienteBean")
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG
            = Logger.getLogger(ClienteBean.class.getName());

    private Cliente cliente;
    private List<Cliente> clientes;
    private Long idSeleccionado;

    /*
     * El DAO no se serializa con la vista. Se reconstruye de forma perezosa
     * después de una activación de sesión.
     */
    private transient ClienteDAO clienteDAO;

    @PostConstruct
    public void init() {
        LOG.info("ClienteBean @PostConstruct: inicializando vista de clientes");
        clienteDAO = new ClienteDAOImpl();
        nuevo();
        cargarClientes();
    }

    /**
     * Carga el cliente indicado por el view-param "id". Se ejecuta después de
     * que JSF enlaza los parámetros de la URL y antes de renderizar la vista.
     */
    public void cargarFormulario() {
        LOG.info("ClienteBean.cargarFormulario id=" + idSeleccionado);
        if (idSeleccionado == null) {
            nuevo();
            return;
        }

        try {
            cliente = dao().buscarPorId(idSeleccionado)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Cliente no encontrado."));
        } catch (DAOException ex) {
            Mensajes.error("No fue posible cargar el cliente.");
            nuevo();
        } catch (IllegalArgumentException ex) {
            Mensajes.error(ex.getMessage());
            nuevo();
        }
    }

    private ClienteDAO dao() {
        if (clienteDAO == null) {
            clienteDAO = new ClienteDAOImpl();
        }
        return clienteDAO;
    }

    public void cargarClientes() {
        try {
            clientes = dao().listar();
            LOG.info("ClienteBean.cargarClientes: " + clientes.size() + " registros");
        } catch (DAOException ex) {
            Mensajes.error("No fue posible cargar los clientes.");
        }
    }

    public void nuevo() {
        idSeleccionado = null;
        cliente = new Cliente();
        cliente.setActivo(true);
    }

    public String irNuevo() {
        nuevo();
        return "formulario?faces-redirect=true";
    }

    public String volverListado() {
        return "lista?faces-redirect=true";
    }

    public String guardar() {
        if (cliente == null) {
            Mensajes.error("No hay datos del cliente para guardar.");
            return null;
        }

        LOG.info("ClienteBean.guardar id=" + cliente.getIdCliente());
        String nombre = limpiar(cliente.getNombre());
        String correo = limpiar(cliente.getCorreo());

        if (nombre.length() < 3
                || !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Mensajes.error("Revise nombre y correo.");
            return null;
        }

        Long id = cliente.getIdCliente();
        try {
            if (dao().existeCorreo(correo, id)) {
                Mensajes.error("El correo ya está registrado.");
                return null;
            }

            cliente.setNombre(nombre);
            cliente.setCorreo(correo);

            if (id == null || id == 0L) {
                long idGenerado = dao().insertar(cliente);
                cliente.setIdCliente(idGenerado);
                Mensajes.info("Cliente registrado correctamente.");
            } else {
                Cliente actual = dao().buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cliente no encontrado."));
                actual.setNombre(nombre);
                actual.setCorreo(correo);
                actual.setActivo(cliente.isActivo());

                if (!dao().actualizar(actual)) {
                    Mensajes.error("No fue posible actualizar el cliente.");
                    return null;
                }
                Mensajes.info("Cliente actualizado correctamente.");
            }

            cargarClientes();
            return "lista?faces-redirect=true";
        } catch (DAOException ex) {
            Mensajes.error("No fue posible guardar el cliente.");
            return null;
        } catch (IllegalArgumentException ex) {
            Mensajes.error(ex.getMessage());
            return null;
        }
    }

    public String editar(Cliente seleccionado) {
        if (seleccionado == null || seleccionado.getIdCliente() == null) {
            Mensajes.error("Seleccione un cliente.");
            return null;
        }

        idSeleccionado = seleccionado.getIdCliente();
        LOG.info("ClienteBean.editar id=" + idSeleccionado);
        cliente = new Cliente(seleccionado);
        return "formulario?faces-redirect=true&id=" + idSeleccionado;
    }

    public void desactivar(Cliente seleccionado) {
        cambiarEstado(seleccionado, false,
                "Cliente desactivado.",
                "No fue posible desactivar el cliente.");
    }

    public void activar(Cliente seleccionado) {
        cambiarEstado(seleccionado, true,
                "Cliente activado.",
                "No fue posible activar el cliente.");
    }

    private void cambiarEstado(Cliente seleccionado, boolean activo,
            String mensajeExito, String mensajeError) {
        if (seleccionado == null || seleccionado.getIdCliente() == null) {
            Mensajes.error("Seleccione un cliente.");
            return;
        }

        LOG.info("ClienteBean.cambiarEstado id="
                + seleccionado.getIdCliente() + " activo=" + activo);
        try {
            if (!dao().cambiarEstado(seleccionado.getIdCliente(), activo)) {
                Mensajes.error("Cliente no encontrado.");
                return;
            }
            Mensajes.info(mensajeExito);
            cargarClientes();
        } catch (DAOException ex) {
            Mensajes.error(mensajeError);
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Long getIdSeleccionado() {
        return idSeleccionado;
    }

    public void setIdSeleccionado(Long idSeleccionado) {
        this.idSeleccionado = idSeleccionado;
    }
}
