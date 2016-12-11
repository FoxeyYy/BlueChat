package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class Peticion implements Serializable {

    /**
     * Constantes identificativas de los tipos de paquete
     */
    public enum TipoPeticion {
        ERROR,
        DESCUBRIMIENTO,
        MENSAJE
    }

    private TipoPeticion tipo;

    public Peticion (TipoPeticion tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve el tipoPeticion del paquete recibido, en forma de una constante definida en esta clase
     * @return el tipoPeticion del paquete
     **/
    public TipoPeticion getTipoPeticion() {
        return tipo;
    }


}
