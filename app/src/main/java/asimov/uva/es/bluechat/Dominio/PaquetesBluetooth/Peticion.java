package asimov.uva.es.bluechat.Dominio.PaquetesBluetooth;

import java.io.Serializable;

/**
 * Implementa una Peticion
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
     * Numero de mensajes a enviar tras recibir la petición
     */
    private final int numeroMensajes;

    /**
     * Tipo de petición
     */
    private final TipoPeticion tipo;

    /**
     * Identificador de un grupo
     */
    private String idGrupo;

    /**
     * Inicializa una petición con un número de mensajes indicado
     * @param numeroMensajes El número de mensajes de la petición
     */
    public Peticion(int numeroMensajes) {
        this.numeroMensajes = numeroMensajes;
        this.tipo = TipoPeticion.MENSAJE;
    }

    /**
     * Inicializa una petición con un número de mensajes indicado, y un identificador de grupo
     * @param numeroMensajes El número de mensajes de la petición
     * @param idGrupo El identificador de grupo
     */
    public Peticion(int numeroMensajes, String idGrupo){
        this.idGrupo = idGrupo;
        this.tipo = TipoPeticion.MENSAJEGRUPO;
        this.numeroMensajes = numeroMensajes;
    }

    /**
     * Devuelve un identificador de grupo
     * @return idGrupo El identificador del grupo
     */
    public String getIdGrupo(){
        return idGrupo;
    }

    /**
     * Devuelve el número de mensajes
     * @return numeroMensajes El número de mensajes
     */
    public int getNumeroMensajes(){
        return numeroMensajes;
    }

    /**
     * Devuelve el tipoPeticion del paquete recibido, en forma de una constante definida en esta clase
     * @return tipo El tipoPeticion del paquete
     **/
    public TipoPeticion getTipoPeticion() {
        return tipo;
    }


}
