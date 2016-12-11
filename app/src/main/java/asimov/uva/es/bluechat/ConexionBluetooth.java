package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;
import asimov.uva.es.bluechat.Dominio.PaquetesBluetooth.Peticion;
import asimov.uva.es.bluechat.Dominio.PaquetesBluetooth.RespuestaPeticion;

/**
 * Hilo encargado de la transmisión de los mensajes una vez se ha establecido la conexión
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ConexionBluetooth extends Thread {

    public enum Modo {
        SERVIDOR,
        CLIENTE_DESCUBRIMIENTO,
        CLIENTE_MENSAJES;
    }

    /**
     * Stream de entrada del {@link BluetoothSocket}
     */
    private ObjectInputStream entrada = null;

    /**
     * Stream de salida del {@link BluetoothSocket}
     */
    private ObjectOutputStream salida = null;

    private final String ERROR = "ERROR";
    private final String CONEXION = "CONEXION";

    private final BluetoothSocket socket;
    private Mensaje mensaje = null;

    /**
     * Modo de ejecucion
     */
    private final Modo modo;

    /**
     * Inicaliza los streams de la conexión bluetooth
     * a partir del socket de la misma
     * @param socket El socket de la conexión
     * @param modo de ejecucion
     */
    public ConexionBluetooth(BluetoothSocket socket, Modo modo){
        Log.d(CONEXION,"CONEXION BUENA");
        this.socket = socket;
        this.modo = modo;

        try {
            /* Primero, crear siempre el output stream, sino se producira un bloqueo indefinido.
            A serialization stream header is read from the stream and verified.
            This constructor will block until the corresponding ObjectOutputStream has written and flushed the header. */
            OutputStream tmpOut = this.socket.getOutputStream();
            salida = new ObjectOutputStream(tmpOut);

            InputStream tmpIn = this.socket.getInputStream();
            entrada = new ObjectInputStream(tmpIn);

        } catch (IOException e){
            Log.e(ERROR,"Thread de conexion no puede obtener los streams");
        }

    }

    /**
     * Inicaliza los streams de la conexión bluetooth
     * a partir del socket de la misma
     * @param socket El socket de la conexión
     * @param modo de ejecucion
     * @param mensaje a enviar
     */
    public ConexionBluetooth(BluetoothSocket socket, Modo modo, Mensaje mensaje){
        this(socket, modo);
        this.mensaje = mensaje;
    }

    /**
     * Recibe el mensaje
     */
    @Override
    public void run(){

        Log.d(CONEXION, "Ejecutando...");

        switch (modo) {
            case CLIENTE_DESCUBRIMIENTO:
                descubrir();
                break;
            case CLIENTE_MENSAJES:
                enviarMensajes();
                break;
            case SERVIDOR:
                servidor();
                break;
            default:
                Log.e(ERROR, "Modo incorrecto");
                break;
        }

        try {
            salida.close();
            entrada.close();
        } catch (IOException e) {
            Log.e(ERROR, "No se pueden cerrar los streams");
        }

    }

    /**
     * Envia mensajes a un servidor
     */
    private void enviarMensajes () {
        Log.d(CONEXION, "Enviando mensajes...");

        solicitarEnvioMensajes();
        if (solicitudAceptada()) {
            enviar(mensaje);
        } else {
            Log.e(ERROR, "Solicitud rechadaza");
        }
    }

    /**
     * Envia solicitudes de descubrimiento a un servidor
     */
    private void descubrir() {
        Log.d(CONEXION, "Enviando...");

        solicitarDescubrimiento();
        if (solicitudAceptada()) {
            recibirDescubrimiento();
        } else {
            Log.e(ERROR, "Solicitud rechadaza");
        }
    }

    /**
     * Recibe peticiones y actua en consecuencia
     */
    private void servidor() {

        Log.d(CONEXION, "Escuchando...");

        try{
            Peticion peticion = (Peticion) entrada.readObject();
            switch (peticion.getTipoPeticion()) {
                case DESCUBRIMIENTO:
                    responderPeticion(true);
                    enviarDescubrimiento();
                    break;
                case MENSAJE:
                    responderPeticion(true);
                    recibirMensaje();
                    break;
                default:
                    Log.e(ERROR, "Peticion invalida");
                    responderPeticion(false);
                    break;
            }

        } catch (IOException e){
            Log.d(ERROR, e.toString());
            Log.d(ERROR, "Error recibiendo info");

        } catch (ClassNotFoundException e) {
            Log.d(ERROR, "No se puede encontrar la clase peticion");
        }

    }

    private void enviarDescubrimiento () {
        Contacto yo = Contacto.getSelf();
        enviar(yo);
    }

    private void recibirDescubrimiento () {
        try {
            Contacto contacto = (Contacto) entrada.readObject();
            MainActivity.getMainActivity().notificar(contacto.getDireccionMac() + ": " + contacto.getNombre()); //TODO guardar base de datos y demas
        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir la respuesta");
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase respuesta");
        }
    }

    private boolean solicitudAceptada () {
        try {
            RespuestaPeticion respuesta = (RespuestaPeticion) entrada.readObject();
            return respuesta.getTipo().equals(RespuestaPeticion.TipoRespuesta.ACEPTAR);
        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir la respuesta");
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase respuesta");
        }

        return false;
    }

    private void solicitarEnvioMensajes () {
        Peticion peticion = new Peticion(Peticion.TipoPeticion.MENSAJE);
        enviar(peticion);
    }

    private void solicitarDescubrimiento () {
        Peticion peticion = new Peticion(Peticion.TipoPeticion.DESCUBRIMIENTO);
        enviar(peticion);
    }

    private void enviar(Serializable objeto) {
        try {
            salida.writeObject(objeto);
        } catch (IOException e) {
            Log.e(ERROR, "No se puede enviar el objeto");
        }
    }

    private void recibirMensaje () {
        try {
            Mensaje mensaje = (Mensaje) entrada.readObject();
            MainActivity.getMainActivity().notificar(mensaje.getEmisor().getNombre() + ": " +mensaje.getContenido()); //TODO guardar base de datos y demas
        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir el mensaje");
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase mensaje");
        }
    }

    private void responderPeticion(boolean aceptada) {
        RespuestaPeticion respuesta;

        if (aceptada) {
            respuesta = new RespuestaPeticion(RespuestaPeticion.TipoRespuesta.ACEPTAR);
        } else {
            respuesta = new RespuestaPeticion(RespuestaPeticion.TipoRespuesta.RECHAZAR);
        }

        enviar(respuesta);
    }

}
