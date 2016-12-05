package asimov.uva.es.bluechat;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import asimov.uva.es.bluechat.Dominio.Contacto;

/**
 * Servicio encargado de eniviar los mensajes pendientes
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class EnvioMensajesPendientes extends Service implements Runnable {

    /**
     * Subproceso de ejecucion
     */
    private Thread hilo;

    private boolean enEjecucion;

    BluetoothAdapter adaptadorBluetooth;

    /**
     * Identificador único y universal
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final String SERVICIO = "SERVICIO";

    @Override
    public void onCreate() {
        Log.d(SERVICIO,"Creando el servicio de envio de mensajes pendientes");
        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (null == hilo) {
            Log.d(SERVICIO, "Hilo lanzado que envia mensajes pendientes");
            hilo = new Thread(this);
            hilo.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("servicio", "stop");
        enEjecucion = false;
        hilo.interrupt();
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {

        enEjecucion = true;

        while (enEjecucion) {
            conectar();
            try {
                hilo.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void conectar() {
        String dispositivo = "7C:91:22:3D:6B:69";
        String mensaje = "Este es un mensaje pendiente";

        // Obtenemos el nuevo dispostivo encontrado
        BluetoothDevice device = adaptadorBluetooth.getRemoteDevice(dispositivo);
        Log.d(SERVICIO, "Conectando con: " + device.getAddress());
        BluetoothSocket socket;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            ConexionBluetooth conexion = new ConexionBluetooth(socket);
            //conexion.start();
            conexion.enviar(new Contacto( "Hector",BluetoothAdapter.getDefaultAdapter().getAddress(), ""));
        } catch (IOException e) {
            Log.d(SERVICIO,"Error preparando el socket cliente");
            e.printStackTrace();
        }

    }

}
