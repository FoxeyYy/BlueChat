package asimov.uva.es.bluechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asimov.uva.es.bluechat.Dominio.Contacto;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_descubrir, container, false);

        dispositivos = new ArrayList<>();
        lista = (LinearLayout) rootView.findViewById(R.id.lista_descubrir);

        setEstadoBarraProgreso(false);

        return rootView;
    }

    /**
     * Añade dispositivos dentro de la vista
     * @param dispositivo a añadir a la vista
     */
    public void anadirDispositivo(Contacto dispositivo, boolean restaurando){
        if (!restaurando) {
            dispositivos.add(dispositivo);
        }
        View tarjeta = inflater.inflate(R.layout.tarjeta_contacto,null);

        TextView nombre = (TextView) tarjeta.findViewById(R.id.nombre_contacto);
        nombre.setText(dispositivo.getNombre());

        TextView mac = (TextView) tarjeta.findViewById(R.id.ultimo_mensaje);
        mac.setText(String.valueOf(dispositivo.getDireccionMac()));

        lista.addView(tarjeta);
        tarjeta.setOnClickListener(this);
        Log.d(TAG,"Añandiendo el nombre: " + dispositivo.getNombre()+" con mac: " + String.valueOf(dispositivo.getDireccionMac()));
    }

    @Override
    public void onClick(View v) {
        Intent intentChat = new Intent(getContext(), ChatActivity.class);
        Contacto dispositivo = dispositivos.get(lista.indexOfChild(v));
        intentChat.putExtra("nombre_contacto", dispositivo.getNombre());
        startActivity(intentChat);
        new ClienteBluetooth(dispositivo.getDireccionMac()).start();
    }

    /**
     * Elimina todas las tarjetas de los dispositivos que se encuentran en la vista
     */
    public void eliminarTarjetas(){
        lista.removeAllViews();
    }

    /**
     * Cambia la visivilidad de la barra de progreso,
     * si no existe dicha barra la llamada quedara sin efecto
     * @param visibilidad La visibilidad a establecer
     */
    public void setEstadoBarraProgreso(boolean visibilidad) {

        if (null == getActivity()) {
            return;
        }

        SwipeRefreshLayout barraProgreso = (SwipeRefreshLayout) getActivity().findViewById(R.id.layout_swipe);

        if (null != barraProgreso){
            barraProgreso.setRefreshing(visibilidad);
            barraProgreso.setEnabled(visibilidad);
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
