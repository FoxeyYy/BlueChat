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
        String dispositivo = "C0:EE:FB:4B:D9:F6";
        String mensaje = "Este es un mensaje pendiente";

        // Obtenemos el nuevo dispostivo encontrado
        BluetoothDevice device = adaptadorBluetooth.getRemoteDevice(dispositivo);
        Log.d(SERVICIO, "Conectando con: " + device.getAddress());
        BluetoothSocket socket;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(ServidorBluetooth.MY_UUID);
            socket.connect();
            ConexionBluetooth conexion = new ConexionBluetooth(socket, ConexionBluetooth.Modo.CLIENTE_DESCUBRIMIENTO);
            //ConexionBluetooth conexion = new ConexionBluetooth(socket, ConexionBluetooth.Modo.CLIENTE_MENSAJES, new Mensaje("Hola, mundo!", new Contacto("Hector", "AA:DD:CC:BB", "sin imagen"), new Date()));
            conexion.start();
            //conexion.enviar(new Contacto( "Hector",BluetoothAdapter.getDefaultAdapter().getAddress(), ""));
        } catch (IOException e) {
            Log.d(SERVICIO,"Error preparando el socket cliente");
            e.printStackTrace();
        }

    }

}
