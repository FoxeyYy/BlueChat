package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class PaqueteDescubrimiento implements Serializable {

    private byte TIPO = PaqueteBluetooth.DESCUBRIMIENTO;
    private String nombre;
    private String foto;

    public PaqueteDescubrimiento (String nombre, String foto) {
        this.nombre = nombre;
        this.foto = foto;
    }

    public String getFoto() {
        return foto;
    }

    public String getNombre() {
        return nombre;
    }
}
