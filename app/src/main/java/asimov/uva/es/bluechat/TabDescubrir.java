package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab que muestra los dispositvos encontrados
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
    private List<BluetoothDevice> dispositvos;

    /**
     * Inflador para inicializar las tarjetas de contactos
     */
    private LayoutInflater inflater;


    private final String TAG = "BLUETOOTH";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_descubrir, container, false);

        dispositvos = new ArrayList<BluetoothDevice>();
        lista = (LinearLayout) rootView.findViewById(R.id.lista_descubrir);

        return rootView;
    }

    /**
     * Añade dispositivos dentro de la vista
     * @param dispositivo a añadir a la vista
     */
    public void anadirDispositivo(BluetoothDevice dispositivo){
        dispositvos.add(dispositvos.size(), dispositivo);
        View tarjeta = inflater.inflate(R.layout.tarjeta_contacto,null);
        TextView nombre = (TextView) tarjeta.findViewById(R.id.nombre_contacto);
        nombre.setText(dispositivo.getName());
        TextView mac = (TextView) tarjeta.findViewById(R.id.ultimo_mensaje);
        mac.setText(dispositivo.getAddress());
        lista.addView(tarjeta);
        tarjeta.setOnClickListener(this);
        Log.d(TAG,"Añandiendo el nombre: " + dispositivo.getName()+" con mac: " + dispositivo.getAddress());
    }

    @Override
    public void onClick(View v) {
        Intent intentChat = new Intent(getContext(), ChatActivity.class);
        BluetoothDevice dispositivo = dispositvos.get(lista.indexOfChild(v));
        intentChat.putExtra("nombre_contacto", dispositivo.getName());
        startActivity(intentChat);
        new ClienteBluetooth(dispositivo).start();
    }

    /**
     * Elimina todas las tarjetas de los dispositivos que se encuentran en la vista
     */
    public void eliminarTarjetas(){
        lista.removeAllViews();
    }

}
