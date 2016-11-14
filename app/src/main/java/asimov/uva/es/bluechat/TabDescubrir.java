package asimov.uva.es.bluechat;

/**
 * Created by Guti on 31/10/2016.
 */

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

public class TabDescubrir extends Fragment implements View.OnClickListener{

    /**
     * Lista donde añadir tarjetas de personas
     */
    private LinearLayout lista;

    /**
     * Dispositvos cercanos descubiertos
     */
    private List<BluetoothDevice> dispositvos;

    private final String TAG = "BLUETOOTH";
    private LayoutInflater inflater;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_descubrir, container, false);

        dispositvos = new ArrayList<BluetoothDevice>();
        lista = (LinearLayout) rootView.findViewById(R.id.lista_descubrir);

        return rootView;
    }

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
        intentChat.putExtra("nombre_contacto", dispositvos.get(lista.indexOfChild(v)).getName());
        startActivity(intentChat);
    }

    public void eliminarTarjetas(){
        lista.removeAllViews();
    }

}
