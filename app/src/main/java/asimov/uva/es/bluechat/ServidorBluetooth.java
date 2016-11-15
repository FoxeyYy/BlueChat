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

    private BluetoothServerSocket socketServidor;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String MENSAJESERVIDOR = "Saludos desde el servdor";
    private final String NOMBRE = "BlueChat";
    private final String CONEXION = "CONEXION";
    private final String ERROR = "ERROR";
    private final String TAG = "BLUETOOTH";

    /**
     * Adaptador bluetooth del dispositivo
     */
    private BluetoothAdapter adaptadorBluetooth;

    public ServidorBluetooth() {
        Log.d(CONEXION,"Creado Servidor");
        BluetoothServerSocket tmp = null;

        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = adaptadorBluetooth.listenUsingInsecureRfcommWithServiceRecord(NOMBRE, MY_UUID);
        } catch (IOException e) {
            Log.d(CONEXION, "Error creando el socket que va a escuchar");
        }
        socketServidor = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;

        //Escuchamos esperando conexciones
        //Llamada bloqueante
        while (true) {
            try {
                socket = socketServidor.accept();
            } catch (IOException e) {
                break;
            }
            //Conexion aceptada
            if (socket != null) {
                Log.d(CONEXION, "Aceptada la conexion nueva en el servidor");
                //Manejo de la conexion en otro hilo diferente
                Conexion conexion = new Conexion(socket);
                conexion.start();

                //Probamos la conexion enviando un mensaje predefinido
                conexion.enviar(MENSAJESERVIDOR.getBytes());

                cancelar();
                break;
            }
        }
    }

    /**
     * Cierra el socket que se encuentra esperando conexiones
     */
    private void cancelar() {
        try {
            socketServidor.close();
        } catch (IOException e) {
            Log.d(ERROR,"El serverSocket se ha cerrado de manera erronea");
        }
    }


}

