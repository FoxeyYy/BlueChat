package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * Implementa la respuesta a una petición
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class RespuestaPeticion implements Serializable {

    /**
     * Enumeración que establece el tipo de respuesta
     */
    public enum TipoRespuesta {
        ACEPTAR,
        RECHAZAR
    }

    /**
     * Tipo de respuesta de una petición
     */
    private final TipoRespuesta tipo;

    /**
     * Inicializa una respuesta a una petición indicando el tipo
     * @param tipo El tipo de respuesta
     */
    public RespuestaPeticion (TipoRespuesta tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve el tipo de la respuesta
     * @return tipo El tipo de la respuesta
     */
    public TipoRespuesta getTipo () {
        return tipo;
    }
}
