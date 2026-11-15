package sv.edu.itca.servicedesk360.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.SeguimientoDAO;
import sv.edu.itca.servicedesk360.modelo.Seguimiento;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class SeguimientoDAOImpl implements SeguimientoDAO {

    @Override
    public int registrar(Connection cn, Seguimiento seguimiento)
            throws DAOException {
        String sql = "INSERT INTO seguimientos (id_ticket, detalle, "
                + "fecha_registro) VALUES (?, ?, NOW())";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, seguimiento.getIdTicket());
            ps.setString(2, seguimiento.getDetalle());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException("Error registrando seguimiento.", ex);
        }
    }

    @Override
    public List<Seguimiento> listarPorTicket(int idTicket)
            throws DAOException {
        String sql = "SELECT id_seguimiento, id_ticket, detalle, "
                + "fecha_registro FROM seguimientos "
                + "WHERE id_ticket = ? "
                + "ORDER BY fecha_registro ASC, id_seguimiento ASC";
        List<Seguimiento> seguimientos = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTicket);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp fecha = rs.getTimestamp("fecha_registro");
                    seguimientos.add(new Seguimiento(
                            rs.getInt("id_seguimiento"),
                            rs.getInt("id_ticket"),
                            rs.getString("detalle"),
                            fecha == null ? null : fecha.toLocalDateTime()));
                }
            }
            return seguimientos;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible listar los seguimientos", ex);
        }
    }
}
