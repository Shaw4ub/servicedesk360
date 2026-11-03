package sv.edu.itca.servicedesk360.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sv.edu.itca.servicedesk360.dao.ClienteDAO;
import sv.edu.itca.servicedesk360.dao.DAOException;
import sv.edu.itca.servicedesk360.modelo.Cliente;
import sv.edu.itca.servicedesk360.persistence.ConexionBD;

public class ClienteDAOImpl implements ClienteDAO {

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("id_cliente"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getBoolean("activo")
        );
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id_cliente, nombre, correo, activo "
                + "FROM clientes ORDER BY id_cliente DESC";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapear(rs));
            }
            return clientes;
        } catch (SQLException ex) {
            throw new DAOException("No fue posible listar clientes", ex);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(long id) {
        String sql = "SELECT id_cliente, nombre, correo, activo "
                + "FROM clientes WHERE id_cliente = ?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapear(rs))
                        : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible consultar el cliente", ex);
        }
    }

    @Override
    public boolean existeCorreo(String correo, Long excluirId) {
        String sql = "SELECT COUNT(*) FROM clientes "
                + "WHERE LOWER(correo) = LOWER(?) "
                + "AND (? IS NULL OR id_cliente <> ?)";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, correo);

            if (excluirId == null) {
                ps.setNull(2, Types.BIGINT);
                ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(2, excluirId);
                ps.setLong(3, excluirId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible validar el correo", ex);
        }
    }

    @Override
    public long insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes(nombre, correo, activo) "
                + "VALUES (?, ?, ?)";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setBoolean(3, cliente.isActivo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new DAOException(
                    "MySQL no devolvió la clave generada");
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible registrar el cliente", ex);
        }
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre=?, correo=?, activo=? "
                + "WHERE id_cliente=?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setBoolean(3, cliente.isActivo());
            ps.setLong(4, cliente.getIdCliente());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible actualizar el cliente", ex);
        }
    }

    @Override
    public boolean eliminar(long id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible eliminar. El cliente puede tener "
                    + "registros relacionados.", ex);
        }
    }

    @Override
    public boolean cambiarEstado(long id, boolean activo) {
        String sql = "UPDATE clientes SET activo=? WHERE id_cliente=?";

        try (Connection cn = ConexionBD.abrir();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DAOException(
                    "No fue posible cambiar el estado", ex);
        }
    }
}
