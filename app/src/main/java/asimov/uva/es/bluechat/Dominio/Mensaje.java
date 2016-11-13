package asimov.uva.es.bluechat.Dominio;

/**
 * Created by hector on 6/11/16.
 * Clase representativa de un mensaje enviado por la App,
 * contiene tanto el mensaje como los metadatos.
 */

public class Mensaje {

    private String mac;
    private int idMensaje;
    private String contenido;
    private String emisor;
    private String receptor;
    private String fecha;

    public Mensaje(String mac, int idMensaje, String contenido, String emisor,
                   String receptor, String fecha) {
        this.mac = mac;
        this.idMensaje = idMensaje;
        this.contenido = contenido;
        this.emisor = emisor;
        this.receptor = receptor;
        this.fecha = fecha;
    }

}
