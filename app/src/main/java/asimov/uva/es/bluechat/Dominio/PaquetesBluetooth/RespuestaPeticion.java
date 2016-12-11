package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class RespuestaPeticion implements Serializable {

    public enum TipoRespuesta {
        ACEPTAR,
        RECHAZAR
    }

    private TipoRespuesta tipo;

    public RespuestaPeticion (TipoRespuesta tipo) {
        this.tipo = tipo;
    }

    public TipoRespuesta getTipo () {
        return tipo;
    }
}
