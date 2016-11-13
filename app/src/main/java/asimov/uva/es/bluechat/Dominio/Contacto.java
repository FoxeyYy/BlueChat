package asimov.uva.es.bluechat.Dominio;

/**
 * Created by hector on 6/11/16.
 * Clase representativa de un contacto de la App,
 * contiene datos identificativos para una persona
 */
public class Contacto {

    private String direccionMac;
    private String nombre;
    private String imagen;

    /**
     * Constructor por defecto
     * @param nombre del contacto
     */
    public Contacto (String direccionMac, String nombre, String imagen) {
        this.direccionMac = direccionMac;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public String getDireccionMac() {
        return direccionMac;
    }

    public String getNombre() {
        return nombre;
    }
}
