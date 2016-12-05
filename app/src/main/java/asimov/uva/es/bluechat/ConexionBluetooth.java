package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import asimov.uva.es.bluechat.Dominio.Contacto;

/**
 * Hilo encargado de la transmisión de los mensajes una vez se ha establecido la conexión
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ConexionBluetooth extends Thread {

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

    private BluetoothSocket socket;

    /**
     * Inicaliza los streams de la conexión bluetooth
     * a partir del socket de la misma
     * @param socket El socket de la conexión
     */
    public ConexionBluetooth(BluetoothSocket socket){
        Log.d(CONEXION,"CONEXION BUENA");

        this.socket = socket;

    }

    /**
     * Recibe el mensaje
     */
    @Override
    public void run(){
        try {
            InputStream tmpIn = null;
            tmpIn = socket.getInputStream();
            entrada = new ObjectInputStream(tmpIn);
        }catch (IOException e){
            Log.d(ERROR,"Thread de conexion no puede obtener los streams");
        }
        Log.d(CONEXION, "Escuchando...");
        Contacto contacto = null;
        int bytes;

        try{
            contacto = (Contacto) entrada.readObject();

            //Obtenemos el String a partir de los bytes obtenidos en el buffer de lectura
            String mensaje = contacto.getNombre() + " " + "recibido!";

            //Notificamos el mensaje a la actividad para que muestre una notificacion por pantalla
            MainActivity.getMainActivity().notificar(mensaje);
            Log.d(CONEXION, mensaje);
        }catch (IOException e){
            Log.d(ERROR, e.toString());
            Log.d(ERROR, "Error recibiendo info");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void enviar(Contacto contacto){
        OutputStream tmpOut = null;
        try {
            tmpOut = socket.getOutputStream();
            salida = new ObjectOutputStream(tmpOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            salida.writeObject(contacto);
            Log.d(CONEXION, "Enviado contacto");
        }catch (IOException e){
            Log.d(ERROR,"Error durante la escritura");
            e.printStackTrace();

        }
    }



}
