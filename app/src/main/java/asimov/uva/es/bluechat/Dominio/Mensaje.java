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

    private String mac;
    private int idMensaje;
    private String contenido;
    private String emisor;
    private String receptor;
    private String fecha;

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param mac Mac del usuario
     * @param idMensaje Identificador del mensaje
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     * @param receptor Receptor del mensaje
     * @param fecha Fecha de envío del mensaje
     */
    public Mensaje(String mac, int idMensaje, String contenido, String emisor,
                   String receptor, String fecha) {
        this.mac = mac;
        this.idMensaje = idMensaje;
        this.contenido = contenido;
        this.emisor = emisor;
        this.receptor = receptor;
        this.fecha = fecha;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
