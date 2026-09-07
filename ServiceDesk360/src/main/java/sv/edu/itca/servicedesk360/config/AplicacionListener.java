package sv.edu.itca.servicedesk360.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import sv.edu.itca.servicedesk360.service.ServicioTickets;
import sv.edu.itca.servicedesk360.storage.DirectorioTicketsEnMemoria;

@WebListener
public class AplicacionListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext contexto = sce.getServletContext();
        DirectorioTicketsEnMemoria directorioTickets
                = new DirectorioTicketsEnMemoria();
        ServicioTickets servicioTickets = new ServicioTickets(
                directorioTickets, directorioTickets);
        contexto.setAttribute("servicioTickets", servicioTickets);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
