package asimov.uva.es.bluechat;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Hilo encargado de la establecer el socket Servidor y escuchar peticiones de conexión
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ServidorBluetooth extends Service implements Runnable {

    /**
     * Subproceso de ejecucion
     */
    private Thread hilo;

    /**
     * Socket del servidor
     */
    private BluetoothServerSocket socketServidor;

    /**
     * Identificador único y universal
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    /**
     * Mensaje predefinido de saludo que envía el servidor una vez se conecta un cliente
     */
    private final String MENSAJESERVIDOR = "Saludos desde el servidor";

    /**
     * Nombre de la aplicación para ofrecer
     */
    private final String NOMBRE = "BlueChat";

    private final String CONEXION = "CONEXION";
    private final String ERROR = "ERROR";

    /**
     * Adaptador bluetooth del dispositivo
     */
    private BluetoothAdapter adaptadorBluetooth;

    /**
     * Inicializa el servidor, creando un socketServidor en modo escucha pasiva
     */
    public ServidorBluetooth() {

        Log.d(CONEXION,"Creado Servidor");

        BluetoothServerSocket tmp = null;

        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();

        try {
            Log.d(CONEXION, "tmp");
            tmp = adaptadorBluetooth.listenUsingInsecureRfcommWithServiceRecord(NOMBRE, MY_UUID);
        } catch (IOException e) {
            Log.d(CONEXION, "Error creando el socket que va a escuchar");
        }
        socketServidor = tmp;

        hilo = new Thread(this);
        hilo.start();

    }

    @Override
    public void run() {

        BluetoothSocket socket;

        //Escuchamos esperando conexciones
        //Llamada bloqueante
        while (true) {
            Log.d(CONEXION, "Servicio en ejecucion");

            try {
                socket = socketServidor.accept();
                Log.d(CONEXION,"Servidor Run" + socket.toString());
            } catch (IOException e) {
                break;
            }
            //ConexionBluetooth aceptada
            Log.d(CONEXION, "Aceptada la conexionBluetooth nueva en el servidor");

            //Manejo de la conexion Bluetooth en otro hilo diferente
            ConexionBluetooth conexionBluetooth = new ConexionBluetooth(socket);
            conexionBluetooth.start();

            //Probamos la conexionBluetooth enviando un mensaje predefinido
            conexionBluetooth.enviar(MENSAJESERVIDOR.getBytes());
        }
    }

    /**
     * Cierra el socket que se encuentra esperando conexiones
     */
    private void cancelar() {
        hilo.interrupt();
        try {
            socketServidor.close();
        } catch (IOException e) {
            Log.d(ERROR,"El serverSocket se ha cerrado de manera erronea");
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        cancelar();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

