package sv.edu.itca.servicedesk360.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones JDBC de laboratorio de la Guía 5.
 *
 * <p>Esta clase es exploratoria. En la Guía 6 sus operaciones se moverán a
 * interfaces DAO y el modelo definitivo.</p>
 */
public class ConsultasLaboratorioJDBC {

    /**
     * Busca un cliente por correo usando una consulta parametrizada.
     *
     * @param correo correo exacto del cliente
     * @return el cliente encontrado o {@code null} si no existe
     * @throws SQLException si ocurre un error de acceso a datos
     */
    public ClienteRegistro buscarClientePorCorreo(String correo)
            throws SQLException {
        String sql = "SELECT id_cliente, nombre, correo, activo "
                + "FROM clientes WHERE correo = ?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new ClienteRegistro(
                        rs.getLong("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getBoolean("activo"));
            }
        }
    }

    /**
     * Inserta un cliente y devuelve la clave generada por MySQL.
     *
     * @param nombre nombre del cliente
     * @param correo correo único del cliente
     * @return id_cliente generado
     * @throws SQLException si la inserción falla o no se devuelve la clave
     */
    public long insertarCliente(String nombre, String correo)
            throws SQLException {
        String sql = "INSERT INTO clientes(nombre, correo) VALUES (?, ?)";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException(
                            "MySQL no devolvió la clave generada.");
                }
                return keys.getLong(1);
            }
        }
    }

    /**
     * Registra un ticket y su seguimiento inicial en una sola transacción.
     * Si cualquiera de las dos inserciones falla, se revierte todo.
     *
     * @param idCliente cliente propietario del ticket
     * @param idEquipo equipo relacionado, o {@code null}
     * @param idTecnico técnico asignado, o {@code null}
     * @param idCategoria categoría existente
     * @param titulo título del ticket
     * @param descripcion descripción del problema
     * @param prioridad BAJA, MEDIA o ALTA
     * @param detalleInicial detalle del primer seguimiento
     * @return id_ticket generado
     * @throws SQLException si la transacción falla y se revierte
     */
    public long registrarTicketConSeguimiento(
            long idCliente, Long idEquipo, Long idTecnico,
            long idCategoria, String titulo, String descripcion,
            String prioridad, String detalleInicial) throws SQLException {

        String sqlTicket = "INSERT INTO tickets "
                + "(id_cliente,id_equipo,id_tecnico,id_categoria,titulo,"
                + "descripcion,prioridad) "
                + "VALUES (?,?,?,?,?,?,?)";
        String sqlSeguimiento
                = "INSERT INTO seguimientos(id_ticket, detalle) VALUES (?,?)";

        try (Connection cn = ConexionBD.abrir()) {
            cn.setAutoCommit(false);
            try {
                long idTicket;

                try (PreparedStatement ps = cn.prepareStatement(
                        sqlTicket, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, idCliente);

                    if (idEquipo == null) {
                        ps.setNull(2, Types.BIGINT);
                    } else {
                        ps.setLong(2, idEquipo);
                    }

                    if (idTecnico == null) {
                        ps.setNull(3, Types.BIGINT);
                    } else {
                        ps.setLong(3, idTecnico);
                    }

                    ps.setLong(4, idCategoria);
                    ps.setString(5, titulo);
                    ps.setString(6, descripcion);
                    ps.setString(7, prioridad);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Sin id_ticket generado.");
                        }
                        idTicket = keys.getLong(1);
                    }
                }

                try (PreparedStatement ps
                        = cn.prepareStatement(sqlSeguimiento)) {
                    ps.setLong(1, idTicket);
                    ps.setString(2, detalleInicial);
                    ps.executeUpdate();
                }

                cn.commit();
                return idTicket;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    /**
     * Lista los tickets con el nombre del cliente y de la categoría.
     *
     * @return tickets ordenados del más reciente al más antiguo
     * @throws SQLException si ocurre un error de lectura
     */
    public List<TicketVista> listarTickets() throws SQLException {
        String sql = "SELECT t.id_ticket, t.titulo, t.prioridad, t.estado, "
                + "t.fecha_creacion, c.nombre AS cliente, "
                + "cat.nombre AS categoria "
                + "FROM tickets t "
                + "JOIN clientes c ON c.id_cliente = t.id_cliente "
                + "JOIN categorias cat ON cat.id_categoria = t.id_categoria "
                + "ORDER BY t.fecha_creacion DESC";

        List<TicketVista> salida = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                salida.add(new TicketVista(
                        rs.getLong("id_ticket"),
                        rs.getString("titulo"),
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_creacion").toLocalDateTime(),
                        rs.getString("cliente"),
                        rs.getString("categoria")));
            }
        }
        return salida;
    }

    /**
     * Cuenta los tickets registrados. Se usa para comprobar el rollback.
     *
     * @return cantidad de filas de tickets
     * @throws SQLException si ocurre un error de lectura
     */
    public long contarTickets() throws SQLException {
        return contar("SELECT COUNT(*) FROM tickets");
    }

    /**
     * Cuenta los seguimientos registrados. Se usa para comprobar el rollback.
     *
     * @return cantidad de filas de seguimientos
     * @throws SQLException si ocurre un error de lectura
     */
    public long contarSeguimientos() throws SQLException {
        return contar("SELECT COUNT(*) FROM seguimientos");
    }

    private long contar(String sql) throws SQLException {
        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
