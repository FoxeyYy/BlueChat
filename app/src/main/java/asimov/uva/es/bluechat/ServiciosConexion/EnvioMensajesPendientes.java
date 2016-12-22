package asimov.uva.es.bluechat.serviciosConexion;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Contacto;
import asimov.uva.es.bluechat.dominio.Mensaje;


/**
 * Servicio encargado de enviar los mensajes pendientes
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class EnvioMensajesPendientes extends Service implements Runnable {

    /**
     * Subproceso de ejecución
     */
    private Thread hilo;

    /**
     * Indica si esta en ejecución
     */
    private boolean enEjecucion;

    /**
     * Adaptador Bluetooth para realizar las tareas
     */
    private BluetoothAdapter adaptadorBluetooth;

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
                hilo.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Conecta un dispositivo con otro
     */
    private void conectar() {

        List<Chat> chats = Chat.getChatsPendientes(getBaseContext());

        Log.d(SERVICIO, "El numero de chats con mensajes pendientes es: " + chats.size());
        for (Chat chat : chats) {
            BluetoothDevice device;
            // Obtenemos el nuevo dispostivo encontrado
            if (!chat.esGrupo()) {
                device = adaptadorBluetooth.getRemoteDevice(chat.getPar().getDireccionMac());
                conexion(device, chat);
            }else{
                List<Contacto> participantes = chat.getParticipantes();
                for(Contacto participante : participantes) {
                    device = adaptadorBluetooth.getRemoteDevice(participante.getDireccionMac());
                    conexion(device, chat);
                }
            }
        }
    }

    /**
     * Establece la conexión de Bluetooth de un dispositivo con un chat
     * @param device El dispositivo
     * @param chat El chat
     */
    private void conexion(BluetoothDevice device, Chat chat){
        BluetoothSocket socket;
        List<Mensaje> mensajes = chat.getHistorial();
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(ServidorBluetooth.MY_UUID);
                socket.connect();

                if(chat.esGrupo()){
                    ConexionBluetooth conexion = new ConexionBluetooth(getBaseContext(), socket, ConexionBluetooth.Modo.CLIENTE_MENSAJES_GRUPO, mensajes);
                    conexion.setIdGrupo(chat.getIdChat());
                    conexion.start();
                }else{
                    ConexionBluetooth conexion = new ConexionBluetooth(getBaseContext(), socket, ConexionBluetooth.Modo.CLIENTE_MENSAJES, mensajes);
                    conexion.start();
                }

            } catch (IOException e) {
                Log.d(SERVICIO,"Error preparando el socket cliente");
                e.printStackTrace();
            }
        }


}
