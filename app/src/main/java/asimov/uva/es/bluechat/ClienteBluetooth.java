package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Hilo encargado de conectarse con el servidor
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ClienteBluetooth implements Callable{

    /**
     * Socket cliente que realiza la conexión con el servidor
     */
    private final BluetoothSocket socket;

    /**
     * Identificador único y universal
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    /**
     * Mensaje predefinido de saludo que envia el cliente una vez se conecta a un servidor
     */
    private final String MENSAJECLIENTE = "Saludos desde el cliente";

    /**
     * Conexion encargada del envio de mensajes
     */
    private ConexionBluetooth conexion;


    private final String ERROR = "ERROR";
    private final String TAG = "BLUETOOTH";

    /**
     * Busca el socket del servidor
     * @param mac La dirección MAC del servidor
     */
    public ClienteBluetooth(String mac){
        BluetoothDevice dispositivo = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        BluetoothSocket tmpSocket = null;
        Log.d(TAG,"CREADO CLIENTE");
        try{
            tmpSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        }catch (IOException e){
            Log.d(ERROR,"Error preparando el socket cliente");
        }
        socket = tmpSocket;

        conectar();

    }

    /**
     * Conecta con el servidor
     */
    public void conectar(){
       //TODO cancelar la conexion dado que hace que los envios vayan lentos

        try {
            socket.connect();
        }catch (IOException e){
            Log.d(ERROR,"Error conectando con el servidor");
            cerrar();
        }

        conexion = new ConexionBluetooth(socket);
        conexion.start();

    }



    /**
     * Cierra el socket asociado a la conexión
     */
    public void cerrar(){
        try{
            socket.close();
        }catch (IOException e){
            Log.d(ERROR, "Error cerrando el socket cliente");
        }
    }

    @Override
    public BluetoothSocket call(){
        return socket;
    }
}
