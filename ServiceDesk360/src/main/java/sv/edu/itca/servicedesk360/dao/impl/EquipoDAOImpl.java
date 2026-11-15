package sv.edu.itca.servicedesk360.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.EquipoDAO;
import sv.edu.itca.servicedesk360.modelo.Equipo;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class EquipoDAOImpl implements EquipoDAO {

    private Equipo mapear(ResultSet rs) throws SQLException {
        return new Equipo(
                rs.getInt("id_equipo"),
                rs.getInt("id_cliente"),
                rs.getString("codigo_inventario"),
                rs.getString("tipo"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getBoolean("activo")
        );
    }

    @Override
    public List<Equipo> listarPorCliente(int idCliente) throws DAOException {
        String sql = "SELECT id_equipo, id_cliente, codigo_inventario, "
                + "tipo, marca, modelo, activo "
                + "FROM equipos WHERE id_cliente = ? AND activo = 1 "
                + "ORDER BY codigo_inventario";
        List<Equipo> equipos = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    equipos.add(mapear(rs));
                }
            }
            return equipos;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible listar los equipos del cliente", ex);
        }
    }

    @Override
    public boolean perteneceACliente(int idEquipo, int idCliente)
            throws DAOException {
        String sql = "SELECT COUNT(*) FROM equipos "
                + "WHERE id_equipo = ? AND id_cliente = ?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible validar el equipo.", ex);
        }
    }
}
