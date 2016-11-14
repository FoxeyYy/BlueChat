package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Hilo encargado de conectarse con el servidor
 */
public class ClienteBluetooth extends Thread {

    private final BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String ERROR = "ERROR";
    private final String TAG = "BLUETOOTH";

    /**
     * Busca el socket del servidor
     * @param dispositivo
     */
    public ClienteBluetooth(BluetoothDevice dispositivo){
        BluetoothSocket tmpSocket = null;
        Log.d(TAG,"CREADO CLIENTE");
        try{
            tmpSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        }catch (IOException e){
            Log.d(ERROR,"Error preparando el socket cliente");
        }
        socket = tmpSocket;
    }

    /**
     * Conecta con el servidor
     */
    @Override
    public void run(){
       //TODO cancelar la conexion dado que hace que los envios vayan lentos

        try {
            socket.connect();
        }catch (IOException e){
            Log.d(ERROR,"Error conectando con el servidor");
            cerrar();
        }

        new Conexion(socket).start();
    }

    /**
     * Cierra el socket asociado a la conexion
     */
    public void cerrar(){
        try{
            socket.close();
        }catch (IOException e){
            Log.d(ERROR, "Error cerrando el socket cliente");
        }
    }
}
