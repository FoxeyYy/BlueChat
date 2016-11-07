package asimov.uva.es.bluechat.Dominio;

import java.util.Date;

/**
 * Created by hector on 6/11/16.
 * Clase representativa de un mensaje enviado por la App,
 * contiene tanto el mensaje como los metadatos.
 */

public class Mensaje {

    private String mensaje;

    /**
     * Metadatos
     */
    private Date fecha;
    private Contacto remitente;
}
