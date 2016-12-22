package asimov.uva.es.bluechat.serviciosConexion;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
     * Subproceso de ejecución
     */
    private final Thread hilo;

    /**
     * Socket del servidor
     */
    private final BluetoothServerSocket socketServidor;

    /**
     * Identificador único y universal
     */
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    /**
     * Inicializa el servidor, creando un socketServidor en modo escucha pasiva
     */
    public ServidorBluetooth() {

        BluetoothServerSocket tmp = null;

        BluetoothAdapter adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();

        try {
            /*
             Nombre de la aplicación para ofrecer
            */
            String NOMBRE = "BlueChat";
            tmp = adaptadorBluetooth.listenUsingInsecureRfcommWithServiceRecord(NOMBRE, MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketServidor = tmp;

        hilo = new Thread(this);
        hilo.start();

    }

    @Override
    public void run() {

        BluetoothSocket socket;

        //Escuchamos esperando conexiones
        //Llamada bloqueante
        while (true) {
            try {
                socket = socketServidor.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            //ConexionBluetooth aceptada

            //Manejo de la conexion Bluetooth en otro hilo diferente
            ConexionBluetooth conexionBluetooth = new ConexionBluetooth(getBaseContext(), socket, ConexionBluetooth.Modo.SERVIDOR);
            conexionBluetooth.start();

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
            e.printStackTrace();
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

