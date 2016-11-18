package asimov.uva.es.bluechat.Dominio;

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

    /**
     * Obtiene la dirección MAC del emisor del mensaje
     * @return mac La dirección MAC del emisor del mensaje
     */
    public String getMac() {
        return mac;
    }

    /**
     * Establece el valor de la dirección MAC del emisor del mensaje
     * @param mac La MAC del emisor del mensaje
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * Obtiene el identificador único del mensaje
     * @return idMensaje El identificador único del mensaje
     */
    public int getIdMensaje() {
        return idMensaje;
    }

    /**
     * Establece el valor del identificador único de mensaje
     * @param idMensaje El identificador del mensaje
     */
    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    /**
     * Obtiene el contenido del mensaje
     * @return contenido El contenido del mensaje
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Establece el valor del contenido del mensaje
     * @param contenido El valor del contenido del mensaje
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    /**
     * Obtiene el nombre del emisor del mensaje
     * @return emisor El emisor del mensaje
     */
    public String getEmisor() {
        return emisor;
    }

    /**
     * Establece el valor del nombre del emisor del mensaje
     * @param emisor El emisor del mensaje
     */
    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    /**
     * Obtiene el nombre del receptor del mensaje
     * @return receptor El nombre del receptor
     */
    public String getReceptor() {
        return receptor;
    }

    /**
     * Establece el valor del nombre del receptor del mensaje
     * @return receptor El nombre del receptor
     */
    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    /**
     * Obtiene la fecha de envío del mensaje
     * @return fecha La fecha de envío del mensaje
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de envío del mensaje
     * @param fecha La fecha de envío del mensaje
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
