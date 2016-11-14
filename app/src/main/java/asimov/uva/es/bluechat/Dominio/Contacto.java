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
     * Inicializa a los parámetros que se indican
     * @param direccionMac La MAC del usuario
     * @param nombre Nombre del usuario
     * @param imagen Imagen del usuario
     */
    public Contacto (String direccionMac, String nombre, String imagen) {
        this.direccionMac = direccionMac;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccionMac(String direccionMac) {
        this.direccionMac = direccionMac;
    }
    public String getDireccionMac() {
        return direccionMac;
    }

    public String getNombre() {
        return nombre;
    }
    public String getImagen() {
        return imagen;
    }
}
