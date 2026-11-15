package sv.edu.itca.servicedesk360.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.TicketDAO;
import sv.edu.itca.servicedesk360.modelo.Ticket;
import sv.edu.itca.servicedesk360.modelo.TicketDetalleDTO;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class TicketDAOImpl implements TicketDAO {

    private static final String SELECT_DETALLE
            = "SELECT t.id_ticket, c.nombre AS cliente, "
            + "CONCAT(e.codigo_inventario, ' - ', COALESCE(e.marca, ''), "
            + "' ', COALESCE(e.modelo, '')) AS equipo, "
            + "cat.nombre AS categoria, tec.nombre AS tecnico, "
            + "t.titulo, t.prioridad, t.estado, t.fecha_creacion "
            + "FROM tickets t "
            + "INNER JOIN clientes c ON c.id_cliente = t.id_cliente "
            + "INNER JOIN equipos e ON e.id_equipo = t.id_equipo "
            + "INNER JOIN categorias cat ON cat.id_categoria = t.id_categoria "
            + "INNER JOIN tecnicos tec ON tec.id_tecnico = t.id_tecnico ";

    @Override
    public int registrar(Connection cn, Ticket ticket) throws DAOException {
        String sql = "INSERT INTO tickets "
                + "(id_cliente, id_equipo, id_tecnico, id_categoria, "
                + "titulo, descripcion, prioridad, estado, fecha_creacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'ABIERTO', NOW())";

        try (PreparedStatement ps = cn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getIdCliente());
            ps.setInt(2, ticket.getIdEquipo());
            ps.setInt(3, ticket.getIdTecnico());
            ps.setInt(4, ticket.getIdCategoria());
            ps.setString(5, ticket.getTitulo());
            ps.setString(6, ticket.getDescripcion());
            ps.setString(7, ticket.getPrioridad());

            if (ps.executeUpdate() != 1) {
                throw new DAOException("No se insertó el ticket.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new DAOException(
                    "MySQL no devolvió el id del ticket.");
        } catch (SQLException ex) {
            throw new DAOException("Error registrando ticket.", ex);
        }
    }

    @Override
    public List<TicketDetalleDTO> listarDetalle(String estado,
            String prioridad, int limite, int offset) throws DAOException {
        String sql = SELECT_DETALLE
                + "WHERE (? IS NULL OR t.estado = ?) "
                + "AND (? IS NULL OR t.prioridad = ?) "
                + "ORDER BY t.fecha_creacion DESC LIMIT ? OFFSET ?";
        List<TicketDetalleDTO> tickets = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, estado);
            ps.setString(3, prioridad);
            ps.setString(4, prioridad);
            ps.setInt(5, limite);
            ps.setInt(6, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapearDetalle(rs));
                }
            }
            return tickets;
        } catch (SQLException ex) {
            throw new DAOException("No fue posible listar los tickets", ex);
        }
    }

    @Override
    public int contar(String estado, String prioridad) throws DAOException {
        String sql = "SELECT COUNT(*) FROM tickets t "
                + "WHERE (? IS NULL OR t.estado = ?) "
                + "AND (? IS NULL OR t.prioridad = ?)";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, estado);
            ps.setString(3, prioridad);
            ps.setString(4, prioridad);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible contar los tickets", ex);
        }
    }

    @Override
    public Optional<TicketDetalleDTO> buscarDetalle(int idTicket)
            throws DAOException {
        String sql = SELECT_DETALLE + "WHERE t.id_ticket = ?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTicket);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapearDetalle(rs))
                        : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible consultar el ticket", ex);
        }
    }

    @Override
    public boolean actualizarEstado(int idTicket, String estadoEsperado,
            String nuevoEstado) throws DAOException {
        String sql = "UPDATE tickets SET estado = ? "
                + "WHERE id_ticket = ? AND estado = ?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idTicket);
            ps.setString(3, estadoEsperado);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible actualizar el estado del ticket", ex);
        }
    }

    private TicketDetalleDTO mapearDetalle(ResultSet rs)
            throws SQLException {
        Timestamp fecha = rs.getTimestamp("fecha_creacion");
        return new TicketDetalleDTO(
                rs.getInt("id_ticket"),
                rs.getString("cliente"),
                rs.getString("equipo"),
                rs.getString("categoria"),
                rs.getString("tecnico"),
                rs.getString("titulo"),
                rs.getString("prioridad"),
                rs.getString("estado"),
                fecha == null ? null : fecha.toLocalDateTime());
    }
}
