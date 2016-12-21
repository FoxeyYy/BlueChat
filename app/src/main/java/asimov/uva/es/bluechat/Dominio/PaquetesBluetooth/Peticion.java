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
        MENSAJE,
        MENSAJEGRUPO
    }

    /**
     * Numero de mensajes a enviar tras recibir la peticion
     */
    private final int numeroMensajes;

    private final TipoPeticion tipo;
    private String idGrupo;

    public Peticion(int numeroMensajes) {
        this.numeroMensajes = numeroMensajes;
        this.tipo = TipoPeticion.MENSAJE;
    }

    public Peticion(int numeroMensajes, String idGrupo){
        this.idGrupo = idGrupo;
        this.tipo = TipoPeticion.MENSAJEGRUPO;
        this.numeroMensajes = numeroMensajes;
    }

    public String getIdGrupo(){
        return idGrupo;
    }

    public int getNumeroMensajes(){
        return numeroMensajes;
    }

    /**
     * Devuelve el tipoPeticion del paquete recibido, en forma de una constante definida en esta clase
     * @return el tipoPeticion del paquete
     **/
    public TipoPeticion getTipoPeticion() {
        return tipo;
    }


}
