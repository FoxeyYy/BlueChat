package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class PaqueteBluetooth implements Serializable{

    /**
     * Constantes identificativas de los tipos de paquete
     */
    public static final byte
            ERROR = -1,
            DESCUBRIMIENTO = 0,
            MENSAJE = 1;

    /**
     * Devuelve el tipo del paquete recibido, en forma de una constante definida en esta clase
     * @param paquete a comprobar
     * @return el tipo del paquete
     */
    public static byte getType(byte[] paquete) {
        switch (paquete[0]) {
            case DESCUBRIMIENTO:
                return DESCUBRIMIENTO;
            case MENSAJE:
                return MENSAJE;
            default:
                return ERROR;
        }
    }


}
