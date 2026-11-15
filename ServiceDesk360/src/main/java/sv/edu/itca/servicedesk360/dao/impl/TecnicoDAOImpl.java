package sv.edu.itca.servicedesk360.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.dao.TecnicoDAO;
import sv.edu.itca.servicedesk360.modelo.Tecnico;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class TecnicoDAOImpl implements TecnicoDAO {

    @Override
    public List<Tecnico> listarActivos() throws DAOException {
        String sql = "SELECT id_tecnico, nombre FROM tecnicos "
                + "WHERE activo = 1 ORDER BY nombre";
        List<Tecnico> tecnicos = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tecnicos.add(new Tecnico(
                        rs.getInt("id_tecnico"),
                        rs.getString("nombre")));
            }
            return tecnicos;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible listar los técnicos activos", ex);
        }
    }
}
