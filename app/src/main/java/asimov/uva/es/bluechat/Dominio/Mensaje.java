package asimov.uva.es.bluechat.Dominio;

import java.util.Date;

/**
 * Mensaje enviado por la App,
 * contiene tanto el mensaje como los metadatos.
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */

public class Mensaje {

    private String mensaje;

    /**
     * Metadatos
     */
    private Date fecha;
    private Contacto remitente;
}
