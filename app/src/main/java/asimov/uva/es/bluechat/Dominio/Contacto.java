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
