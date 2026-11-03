package sv.edu.itca.servicedesk360.dao;

import java.util.List;
import java.util.Optional;
import sv.edu.itca.servicedesk360.modelo.Cliente;

public interface ClienteDAO {

    List<Cliente> listar();

    Optional<Cliente> buscarPorId(long id);

    boolean existeCorreo(String correo, Long excluirId);

    long insertar(Cliente cliente);

    boolean actualizar(Cliente cliente);

    boolean eliminar(long id);

    boolean cambiarEstado(long id, boolean activo);
}
