package sv.edu.itca.servicedesk360.persistence;

import java.sql.SQLException;

/**
 * Demostración ejecutable de las prácticas de la Guía 5.
 *
 * <p>Inserta un cliente de laboratorio, comprueba la parametrización, ejecuta
 * una transacción válida y otra que debe revertirse. No forma parte de la
 * aplicación web.</p>
 */
public class PruebaLaboratorio {

    public static void main(String[] args) {
        ConsultasLaboratorioJDBC laboratorio = new ConsultasLaboratorioJDBC();

        try {
            String correo = "lab." + System.currentTimeMillis()
                    + "@demo.local";

            long idCliente = laboratorio.insertarCliente(
                    "Cliente de laboratorio", correo);
            System.out.println("Cliente insertado: " + idCliente);

            ClienteRegistro encontrado
                    = laboratorio.buscarClientePorCorreo(correo);
            System.out.println("Cliente recuperado: " + encontrado);

            ClienteRegistro inyeccion
                    = laboratorio.buscarClientePorCorreo("' OR '1'='1");
            System.out.println(inyeccion == null
                    ? "Entrada tratada como dato: sin coincidencias"
                    : "Revisar implementación");

            long ticketsAntes = laboratorio.contarTickets();
            long seguimientosAntes = laboratorio.contarSeguimientos();
            System.out.println("Antes de la transacción: tickets="
                    + ticketsAntes + ", seguimientos=" + seguimientosAntes);

            long idTicket = laboratorio.registrarTicketConSeguimiento(
                    idCliente, null, null, 1L,
                    "Error al ingresar al portal",
                    "El usuario recibe un mensaje al autenticar.",
                    "MEDIA",
                    "Ticket registrado desde la Guía 5");
            System.out.println("Ticket confirmado: " + idTicket);

            System.out.println("Después de la transacción válida: tickets="
                    + laboratorio.contarTickets() + ", seguimientos="
                    + laboratorio.contarSeguimientos());

            try {
                laboratorio.registrarTicketConSeguimiento(
                        idCliente, null, null, 999999L,
                        "Transacción que debe fallar",
                        "La categoría no existe.",
                        "BAJA",
                        "Este seguimiento no debe persistir");
                System.out.println("ERROR: la transacción inválida no falló.");
            } catch (SQLException ex) {
                System.out.println("Fallo controlado: " + ex.getMessage());
            }

            System.out.println("Después del rollback: tickets="
                    + laboratorio.contarTickets() + ", seguimientos="
                    + laboratorio.contarSeguimientos());

            System.out.println("Listado de tickets:");
            for (TicketVista vista : laboratorio.listarTickets()) {
                System.out.println("  " + vista);
            }
        } catch (SQLException ex) {
            System.err.println("Error de laboratorio: " + ex.getMessage());
        }
    }
}
