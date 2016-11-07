package asimov.uva.es.bluechat.Dominio;

/**
 * Created by hector on 6/11/16.
 * Clase representativa de un contacto de la App,
 * contiene datos identificativos para una persona
 */
public class Contacto {

    private String nombre;
    private long direccionMac;

    /**
     * Constructor por defecto
     * @param nombre del contacto
     */
    public Contacto (String nombre) {
        this.nombre = nombre;
    }

    public long getDireccionMac() {
        return direccionMac;
    }

    public String getNombre() {
        return nombre;
    }
}
