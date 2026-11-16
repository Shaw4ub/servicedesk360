package sv.edu.itca.servicedesk360.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Utilidad mínima para publicar mensajes globales de JSF.
 *
 * La vista no conoce SQL ni reglas de persistencia: solo muestra los mensajes
 * que el Managed Bean agrega mediante esta clase.
 */
public final class Mensajes {

    private Mensajes() {
    }

    public static void info(String texto) {
        agregar(FacesMessage.SEVERITY_INFO, texto);
    }

    public static void error(String texto) {
        agregar(FacesMessage.SEVERITY_ERROR, texto);
    }

    private static void agregar(FacesMessage.Severity severidad, String texto) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (contexto != null) {
            contexto.addMessage(null,
                    new FacesMessage(severidad, texto, null));
            try {
                // Conserva el mensaje cuando la acción termina en redirect.
                contexto.getExternalContext().getFlash().setKeepMessages(true);
            } catch (IllegalStateException ex) {
                // Sin flash el mensaje sigue disponible para el render actual.
            }
        }
    }
}
