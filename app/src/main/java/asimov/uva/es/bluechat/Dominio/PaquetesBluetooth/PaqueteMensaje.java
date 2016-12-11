package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class PaqueteMensaje implements Serializable {

    private String mensaje;

    public PaqueteMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
