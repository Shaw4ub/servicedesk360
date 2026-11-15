package sv.edu.itca.servicedesk360.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.itca.servicedesk360.dao.CategoriaDAO;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.modelo.Categoria;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public List<Categoria> listarActivas() throws DAOException {
        String sql = "SELECT id_categoria, nombre FROM categorias "
                + "ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")));
            }
            return categorias;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible listar las categorías", ex);
        }
    }
}
