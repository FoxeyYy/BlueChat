package asimov.uva.es.bluechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Mensaje;

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

            mostrarNombreChat(tarjeta, chat);
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
    private void mostrarNombreChat(View vista, Chat chat) {
        ((TextView)vista.findViewById(R.id.nombre_contacto)).setText(chat.getNombre());
    }

    /**
     * Muestra el ultimo mensaje de un chat
     * @param vista a modificar
     * @param chat a mostrar
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
     * @param vista a modificar
     * @param chat a mostrar
     */
    private void mostrarImagen(View vista, Chat chat){
        if (chat.esGrupo()) {
            return;
        }

        ImageView imagen = (ImageView)vista.findViewById(R.id.foto_contacto);
        imagen.setImageURI(Uri.parse(chat.getPar().getImagen()));
    }

    @Override
    public void onClick(View v) {
        Chat chat = chats.get(lista.indexOfChild(v));
        Intent intentChat;
        if (!chat.esGrupo()) {
            intentChat = new Intent(getContext(), ChatActivity.class);
        } else {
            intentChat = new Intent(getContext(), ActivityChatGrupal.class);
        }
        intentChat.putExtra("chat", chat);
        startActivity(intentChat);
    }
}
