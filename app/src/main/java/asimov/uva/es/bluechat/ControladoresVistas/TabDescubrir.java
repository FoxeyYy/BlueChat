package asimov.uva.es.bluechat.controladoresVistas;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Contacto;
import asimov.uva.es.bluechat.R;
import asimov.uva.es.bluechat.serviciosConexion.EnvioMensajesPendientes;
import asimov.uva.es.bluechat.serviciosConexion.ServidorBluetooth;

/**
 * Tab que muestra los dispositivos encontrados
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class TabDescubrir extends Fragment implements View.OnClickListener{

    /**
     * Lista donde añadir tarjetas de personas
     */
    private LinearLayout lista;

    /**
     * Dispositvos cercanos descubiertos
     */
    private List<Contacto> dispositivos;

    /**
     * Inflador para inicializar las tarjetas de contactos
     */
    private LayoutInflater inflater;

    private final String TAG = "BLUETOOTH";

    /**
     * Receptor de información de los dispositivos descubiertos
     */
    private final BroadcastReceiver receptorBluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // Descubrimos un nuevo dispositivo
            switch (action) {
                case (BluetoothDevice.ACTION_FOUND):

                    // Obtenemos el nuevo dispostivo encontrado
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Contacto contacto = Contacto.getContacto(getContext(), device);

                    if (!dispositivos.contains(contacto)) {
                        Log.d(TAG, "Descubierto dispositivo " + device.getAddress());
                        anadirDispositivo(contacto, false);
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "EMPEZANDO A DESCUBRIR");
                    getActivity().stopService(new Intent(getContext(), EnvioMensajesPendientes.class));
                    eliminarDispositivosDescubiertos();
                    setEstadoBarraProgreso(true);
                    break;
                //Finaliza el descubrimiento, oculta la barra de progreso
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "TERMINANDO DESCUBRIMIENTO");
                    setEstadoBarraProgreso(false);
                    getActivity().startService(new Intent(getContext(), EnvioMensajesPendientes.class));
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            getActivity().stopService(new Intent(getContext(), ServidorBluetooth.class));
                            getActivity().stopService(new Intent(getContext(), EnvioMensajesPendientes.class));
                            break;
                    }

            }
        }
    };

    /**
     * Implementa la funcionalidad de deslizamiento de la vista hacia abajo para comenzar a buscar
     * dispositivos
     */
    private final SwipeRefreshLayout.OnRefreshListener receptorGestoActualizar = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(receptorBluetooth, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receptorBluetooth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_descubrir, container, false);

        dispositivos = new ArrayList<>();
        lista = (LinearLayout) rootView.findViewById(R.id.lista_descubrir);

        ((SwipeRefreshLayout) rootView.findViewById(R.id.layout_swipe)).setOnRefreshListener(receptorGestoActualizar);

        setEstadoBarraProgreso(false);

        return rootView;
    }

    /**
     * Añade dispositivos dentro de la vista
     * @param dispositivo El dispositivo a añadir a la vista
     */
    private void anadirDispositivo(Contacto dispositivo, boolean restaurando){
        if (!restaurando) {
            dispositivos.add(dispositivo);
        }
        View tarjeta = inflater.inflate(R.layout.tarjeta_contacto,lista, false);

        TextView nombre = (TextView) tarjeta.findViewById(R.id.nombre_contacto);
        nombre.setText(dispositivo.getNombre());

        TextView mac = (TextView) tarjeta.findViewById(R.id.ultimo_mensaje);
        mac.setText(String.valueOf(dispositivo.getDireccionMac()));

        ImageView imagen = (ImageView) tarjeta.findViewById(R.id.foto_contacto);
        String avatarContacto = dispositivo.getImagen();
        if(avatarContacto !=null && !avatarContacto.isEmpty())
            imagen.setImageURI(Uri.parse(dispositivo.getImagen()));
        else{
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            imagen.setImageBitmap(image);
        }

        lista.addView(tarjeta);
        tarjeta.setOnClickListener(this);
        Log.d(TAG,"Añandiendo el nombre: " + dispositivo.getNombre()+" con mac: " + String.valueOf(dispositivo.getDireccionMac()));
    }

    @Override
    public void onClick(View v) {
        Intent intentChat = new Intent(getContext(), ActividadChatIndividual.class);
        Contacto contacto = dispositivos.get(lista.indexOfChild(v));
        Chat chat = contacto.getChat(getContext());
        if(null == chat){
            chat = new Chat(contacto);
        }
        intentChat.putExtra("chat", chat);
        startActivity(intentChat);
    }

    /**
     * Elimina todas las tarjetas de los dispositivos que se encuentran en la vista
     */
    private void eliminarDispositivosDescubiertos() {
        dispositivos.clear();
        lista.removeAllViews();
    }

    /**
     * Cambia la visivilidad de la barra de progreso,
     * si no existe dicha barra la llamada quedara sin efecto
     * @param visibilidad La visibilidad a establecer
     */
    private void setEstadoBarraProgreso(boolean visibilidad) {

        if (null == getActivity()) {
            return;
        }

        SwipeRefreshLayout barraProgreso = (SwipeRefreshLayout) getActivity().findViewById(R.id.layout_swipe);

        if (null != barraProgreso){
            barraProgreso.setRefreshing(visibilidad);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Dispositivos", (ArrayList<? extends Parcelable>) dispositivos);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (null == savedInstanceState) {
            return;
        }

        dispositivos = savedInstanceState.getParcelableArrayList("Dispositivos");
        Log.e("asdasd", "cargado");


        for(Contacto dispositivo: dispositivos) {
            anadirDispositivo(dispositivo, true);
        }
    }




}
