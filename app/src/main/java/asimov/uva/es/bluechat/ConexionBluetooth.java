package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Hilo encargado de la transmision de los mensajes una vez se ha establecido la conexion
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ConexionBluetooth extends Thread {

    private final BluetoothSocket socket;
    private final InputStream entrada;
    private final OutputStream salida;
    private final String ERROR = "ERROR";
    private final String CONEXION = "CONEXION";
    private final String TAG = "BLUETOOTH";

    public ConexionBluetooth(BluetoothSocket socket){
        Log.d(CONEXION,"CONEXION BUENA");
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }catch (IOException e){
            Log.d(ERROR,"Thread de conexion no puede obtener los streams");
        }

        entrada = tmpIn;
        salida = tmpOut;

    }

    /**
     * Recibe el mensaje
     */
    @Override
    public void run(){
        Log.d(CONEXION, "Escuchando...");
        byte[] buffer = new byte[1024];
        int bytes;

        try{
            bytes = entrada.read(buffer);
            if( bytes>0 ) {

                //Obtenemos el String a partir de los bytes obtenidos en el buffer de lectura
                String mensaje = new String(buffer, "UTF-8").substring(0,bytes);

                //Notificamos el mensaje a la actividad para que muestre una notificacion por pantalla
                MainActivity.getMainActivity().notificar(mensaje);
                Log.d(CONEXION, mensaje);
            }
        }catch (IOException e){
            Log.d(ERROR, "Error recibiendo info");

        }
    }

    /**
     * Envia el mensaje
     * @param mensaje a enviar
     */
    public void enviar(byte[] mensaje){
        try {
            salida.write(mensaje);

        }catch (IOException e){
            Log.d(ERROR,"Error durante la escritura");
            e.printStackTrace();

        }
    }



}
