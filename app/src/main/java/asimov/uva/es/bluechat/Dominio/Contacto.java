package asimov.uva.es.bluechat.Dominio;

/**
 * Contacto de la App,
 * contiene datos identificativos para una persona
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class Contacto {

    /**
     * Nombre del contacto
     */
    private String nombre;

    /**
     * Direcion mac asociado al contacto
     */
    private long direccionMac;

    /**
     * Constructor por defecto
     * @param nombre del contacto
     */
    public Contacto (String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la direccion mac del contacto
     * @return direccion mac del contacto
     */
    public long getDireccionMac() {
        return direccionMac;
    }

    /**
     * Obtiene el nombre del contacto
     * @return nombre del contacto
     */
    public String getNombre() {
        return nombre;
    }
}
