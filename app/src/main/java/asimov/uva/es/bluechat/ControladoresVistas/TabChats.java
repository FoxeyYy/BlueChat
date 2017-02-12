package asimov.uva.es.bluechat.controladoresVistas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.R;
import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Mensaje;

/**
 * Tab que muestra los chats con los que se ha establecido una conversación previa
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class TabChats extends Fragment implements View.OnClickListener {

    /**
     * Muestra la lista de chats con los que se ha mantenido una conversación previa y el último
     * mensaje intercambiado en cada uno de ellos
     */
    private final BroadcastReceiver receptorMensajes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Chat chat = intent.getParcelableExtra("chat");

            int posicion = chats.indexOf(chat);
            View tarjeta = lista.getChildAt(posicion);

            if (chats.contains(chat)) {
                chats.set(posicion, chat);
            }

            if (null == tarjeta) {
                tarjeta = getActivity().getLayoutInflater().inflate(R.layout.tarjeta_contacto, lista, false);
                mostrarNombreChat(tarjeta, chat);
                mostrarImagen(tarjeta, chat);
                tarjeta.setOnClickListener(TabChats.this);
                chats.add(chat);
                lista.addView(tarjeta);
            }

            mostrarUltimoMensaje(tarjeta,chat);

        }
    };

    /**
     * Lista donde añadir tarjetas de personas
     */
    private LinearLayout lista;

    /**
     * Lista de chats con contactos
     */
    private List<Chat> chats;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);

        lista = (LinearLayout) rootView.findViewById(R.id.lista_chats);
        chats = Chat.getChats(getContext());
        actualizar();


        return rootView;
    }

    /**
     * Actualiza la vista para mostrar los chats actualizados
     */
    private void actualizar(){
        lista.removeAllViews();
        for(int i = 0; i < chats.size(); i++) {
            View tarjeta = getActivity().getLayoutInflater().inflate(R.layout.tarjeta_contacto, lista, false);
            Chat chat = chats.get(i);

            mostrarNombreChat(tarjeta, chat);
            mostrarUltimoMensaje(tarjeta, chat);
            mostrarImagen(tarjeta,chat);

            lista.addView(tarjeta);
            tarjeta.setOnClickListener(this);
        }
    }

    /**
     * Muestra el nombre de un chat
     * @param vista La vista a modificar
     * @param chat El chat del que mostrar la información
     */
    private void mostrarNombreChat(View vista, Chat chat) {
        ((TextView)vista.findViewById(R.id.nombre_contacto)).setText(chat.getNombre());
    }

    /**
     * Muestra el último mensaje de un chat
     * @param vista La vista a modificar
     * @param chat El chat del que mostrar la información
     */
    private void mostrarUltimoMensaje(View vista, Chat chat) {
        List<Mensaje> msgs = chat.getHistorial();

        if (msgs.isEmpty()) {
            return;
        }

        String ultimoMensaje = msgs.get(msgs.size()-1).getContenido();
        ((TextView)vista.findViewById(R.id.ultimo_mensaje)).setText(ultimoMensaje);
    }

    /**
     * Muestra la imagen del contacto
     * @param vista La vista a modificar
     * @param chat El chat del que mostrar la información
     */
    private void mostrarImagen(View vista, Chat chat){
        ImageView imagen = (ImageView)vista.findViewById(R.id.foto_contacto);
        if (chat.esGrupo()) {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            imagen.setImageBitmap(image);
            return;
        }


        String avatarContacto = chat.getPar().getImagen();
        if(avatarContacto !=null && !avatarContacto.isEmpty())
            imagen.setImageURI(Uri.parse(chat.getPar().getImagen()));
        else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            imagen.setImageBitmap(image);
        }
    }

    @Override
    public void onClick(View v) {
        Chat chat = chats.get(lista.indexOfChild(v));
        Intent intentChat;
        if (!chat.esGrupo()) {
            intentChat = new Intent(getContext(), ActividadChatIndividual.class);
        } else {
            intentChat = new Intent(getContext(), ActivityChatGrupal.class);
        }
        intentChat.putExtra("idChat", chat.getIdChat());
        startActivity(intentChat);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receptorMensajes);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        chats = Chat.getChats(getContext());
        actualizar();
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter("mensajeNuevo");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receptorMensajes,filter);
        super.onCreate(savedInstanceState);
    }

}
