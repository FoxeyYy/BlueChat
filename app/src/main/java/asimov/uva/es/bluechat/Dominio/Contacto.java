package asimov.uva.es.bluechat.Dominio;

/**
 * Created by hector on 6/11/16.
 * Clase representativa de un contacto de la App,
 * contiene datos identificativos para una persona
 */
public class Contacto {

    private String nombre;
    private String direccionMac;

    /**
     * Constructor por defecto
     * @param nombre del contacto
     */
    public Contacto (String direccionMac, String nombre) {
        this.direccionMac = direccionMac;
        this.nombre = nombre;
    }

    public String getDireccionMac() {
        return direccionMac;
    }

    public String getNombre() {
        return nombre;
    }
}
