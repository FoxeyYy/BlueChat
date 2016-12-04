package asimov.uva.es.bluechat;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

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

    BroadcastReceiver receptorBluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // Descubrimos un nuevo dispositivo
            switch (action) {
                case (BluetoothDevice.ACTION_FOUND):

                    //STUB mensajes pendientes
                    String dispositivo = "DA:23:46:03:35:1E";
                    String mensaje = "Este es un mensaje pendiente";

                    // Obtenemos el nuevo dispostivo encontrado
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d(SERVICIO, "Descubierdo dispositivo: " + device.getName());
                    if (dispositivo.equals(device.getAddress())) {
                        Log.d(SERVICIO, "Descubierto dispositivo " + device.getAddress());
                        ClienteBluetooth cliente = new ClienteBluetooth(device.getAddress());
                        BluetoothSocket socket = cliente.call();
                        ConexionBluetooth conexion = new ConexionBluetooth(socket);
                        conexion.start();
                        conexion.enviar(mensaje.getBytes());

                    }
                    break;

                //
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(SERVICIO, "EMPEZANDO A DESCUBRIR");
                    break;

                //Finaliza el descubrimiento, oculta la barra de progreso
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(SERVICIO, "TERMINANDO DESCUBRIMIENTO");
                    break;
            }
        }
    };

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
        //adaptadorBluetooth.cancelDiscovery();
        //unregisterReceiver(receptorBluetooth);
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

        /*IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receptorBluetooth, filter);*/

        while (enEjecucion) {
            //adaptadorBluetooth.startDiscovery();
            conectar();
            try {
                hilo.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void conectar() {
        String dispositivo = "DA:23:46:03:35:1E";
        String mensaje = "Este es un mensaje pendiente";

        // Obtenemos el nuevo dispostivo encontrado
        BluetoothDevice device = adaptadorBluetooth.getRemoteDevice(dispositivo);
        Log.d(SERVICIO, "Conectando con: " + device.getAddress());
        BluetoothSocket socket;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            ConexionBluetooth conexion = new ConexionBluetooth(socket);
            conexion.start();
            conexion.enviar(mensaje.getBytes());
        } catch (IOException e) {
            Log.d(SERVICIO,"Error preparando el socket cliente");
            e.printStackTrace();
        }

    }

}
