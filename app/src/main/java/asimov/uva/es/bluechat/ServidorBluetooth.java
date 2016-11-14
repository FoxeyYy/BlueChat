package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Hilo encargado de la establecer el socket Servidor y escuchar peticiones de conexion
 */
public class ServidorBluetooth extends Thread {

    private BluetoothServerSocket mmServerSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String NOMBRE = "BlueChat";
    private final String ERROR = "PROBLEMA";
    private final String TAG = "BLUETOOTH";

    /**
     * Adaptador bluetooth del dispositivo
     */
    private BluetoothAdapter adaptadorBluetooth;

    public ServidorBluetooth() {
        Log.d(TAG,"Creado Servidor");
        BluetoothServerSocket tmp = null;

        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = adaptadorBluetooth.listenUsingInsecureRfcommWithServiceRecord(NOMBRE, MY_UUID);
        } catch (IOException e) {
            Log.d(ERROR, "Error creando el socket que va a escuchar");
        }
        mmServerSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;


        //Escuchamos esperando conexciones
        //Llamada bloqueante
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            //Conexion aceptada
            if (socket != null) {
                //Manejo de la conexion en otro hilo diferente
                new Conexion(socket);
                cancelar();
                break;
            }
        }
    }

    /**
     * Cierra el socket que se encuentra esperando conexiones
     */
    public void cancelar() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.d(ERROR,"El serverSocket no se ha cerrado de manera erronea");
        }
    }


}

