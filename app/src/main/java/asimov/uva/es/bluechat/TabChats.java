package asimov.uva.es.bluechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;
import asimov.uva.es.bluechat.sqllite.DBOperations;

/**
 * Tab que muestra los chats con los que se ha establecido
 * una conversacion previa
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class TabChats extends Fragment implements View.OnClickListener {

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
        chats = Chat.getChats(getContext()); //TODO comprobar si al introducir uno nuevo se genera en la vista, o hay que refrescar

        for(int i = 0; i < chats.size(); i++) {
            View tarjeta = inflater.inflate(R.layout.tarjeta_contacto, null);
            Chat chat = chats.get(i);

            mostrarNombreContacto(tarjeta, chat);
            mostrarUltimoMensaje(tarjeta, chat);
            mostrarImagen(tarjeta,chat);

            lista.addView(tarjeta);
            tarjeta.setOnClickListener(this);
        }

        return rootView;
    }

    /**
     * Muestra el nombre de un chat
     * @param vista a modificar
     * @param chat a mostrar
     */
    private void mostrarNombreContacto(View vista, Chat chat) {
        ((TextView)vista.findViewById(R.id.nombre_contacto)).setText(chat.getPar().getNombre());
    }

    /**
     * Muestra el ultimo mensaje de un chat
     * @param vista a modificar
     * @param chat a mostrar
     */
    private void mostrarUltimoMensaje(View vista, Chat chat) {
        List<Mensaje> msgs = chat.getHistorial();
        String ultimoMensaje = msgs.get(msgs.size()-1).getContenido();
        ((TextView)vista.findViewById(R.id.ultimo_mensaje)).setText(ultimoMensaje);
    }

    /**
     * Muestra la imagen del contacto
     * @param vista a modificar
     * @param chat a mostrar
     */
    private void mostrarImagen(View vista, Chat chat){
        ImageView imagen = (ImageView)vista.findViewById(R.id.foto_contacto);
        imagen.setImageURI(Uri.parse(chat.getPar().getImagen()));
    }

    @Override
    public void onClick(View v) {
        Intent intentChat = new Intent(getContext(), ChatActivity.class);
        Chat chat = chats.get(lista.indexOfChild(v));
        intentChat.putExtra("chat", chat);
        startActivity(intentChat);
    }
}
