package asimov.uva.es.bluechat.controladoresVistas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.R;
import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Contacto;
import asimov.uva.es.bluechat.dominio.Mensaje;

/**
 * Actividad para los chats interactivos
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ActividadChatIndividual extends ActividadChatBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle params = getIntent().getExtras();

        Chat chat;
        String idChat = params.getString("idChat");

        //El chat ha sido creado desde TabDescubrir
        //No sabemos si es persistente o no.
        if(idChat ==null){
            chat = params.getParcelable("chat");
            Chat chatActualizado = chat.getPar().getChat(this);
            if(chatActualizado != null)
                chat = chatActualizado;
        }else {
            //El chat ha sido creado desde el TabChats
            // sabemos que se encuentra en la bd
            chat = Chat.getChatById(this, idChat);
        }
        setChat(chat);
        ImageView avatar = (ImageView)findViewById(R.id.avatar_contacto);
        String imagen = getChat().getPar().getImagen();
        if(imagen.isEmpty()) {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            avatar.setImageBitmap(image);
        } else
            avatar.setImageURI(Uri.parse(imagen));

        ((TextView) findViewById(R.id.nombre_contacto)).setText(getChat().getPar().getNombre());
        findViewById(R.id.boton_enviar).setOnClickListener(this);
        findViewById(R.id.boton_foto).setOnClickListener(this);

        setListaMensajes((LinearLayout) findViewById(R.id.lista_mensajes));
        setCampoTexto((TextView) findViewById(R.id.texto));

        List<Mensaje> historial = getChat().getHistorial();

        Contacto myself = Contacto.getSelf(getBaseContext());
        for(Mensaje msg: historial) {
            if(msg.getEmisor().equals(myself))
                mostrarMensajeEnviado(msg);
            else
                mostrarMensajeRecibido(msg);
        }

        scrollAlUltimo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_anadir:
                crearContacto();
                break;
            case R.id.action_actualizar:
                actualizarContacto();
                break;
            default:
                Log.e("ERROR", "Opcion desconocida");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Inicializa la actividad para crear un contacto nuevo a partir de los datos del chat actual
     */
    private void crearContacto() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, "BlueChat");
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, getChat().getPar().getDireccionMac());
        startActivity(intent);
    }

    /**
     * Inicializa la actividad para actualizar un contacto existente con los datos del chat actual
     */
    private void actualizarContacto() {
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, "BlueChat");
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, getChat().getPar().getDireccionMac());
        startActivity(intent);
    }

    @Override
    protected void enviar() {

        if (!getChat().esPersistente()) {
            getChat().guardar(getBaseContext());
        }

        Contacto contacto = getChat().getPar();
        if (!contacto.esPersistente()) {
            contacto.guardar(getBaseContext());
        }
        super.enviar();

    }

}
