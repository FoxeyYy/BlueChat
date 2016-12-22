package asimov.uva.es.bluechat.controladoresVistas;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.R;
import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Contacto;
import asimov.uva.es.bluechat.dominio.Mensaje;

/**
 * Actividad para los chats grupales.
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ActivityChatGrupal extends ActividadChatBase {

    /**
     * Constantes para la creacion de un grupo
     */
    public static final String CONTACTOS = "Participantes";
    public static final String NOMBRE_GRUPO = "Nombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_grupal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            setChat(Chat.getChatGrupal(getBaseContext(), savedInstanceState.getString("chat")));
        } else {
            Bundle params = getIntent().getExtras();
            Chat chat = Chat.getChatGrupal(this, params.getString("idChat"));
            setChat(chat);

            if (null == getChat()) {
                nuevoChat(params);
            }
        }

        findViewById(R.id.boton_enviar).setOnClickListener(this);
        findViewById(R.id.boton_foto).setOnClickListener(this);

        setListaMensajes((LinearLayout) findViewById(R.id.lista_mensajes));
        setCampoTexto((TextView) findViewById(R.id.texto));

        String nombres = "";

        for (Contacto contacto : getChat().getParticipantes()) {
            nombres += contacto.getNombre()+ ", ";
        }

        nombres = nombres.substring(0, nombres.length() - 2);
        ((TextView) findViewById(R.id.nombre_grupo)).setText(getChat().getNombre());
        ((TextView) findViewById(R.id.participantes)).setText(nombres);

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

    /**
     * Permite crear un chat grupal con un conjunto de participantes y un nombre de grupo
     * @param params La lista de participantes del grupo y el nombre asignado al mismo
     */
    private void nuevoChat (Bundle params) {
        List<Contacto> participantes = params.getParcelableArrayList(CONTACTOS);
        String nombreGrupo = params.getString(NOMBRE_GRUPO);

        setChat(new Chat(nombreGrupo, participantes));
        getChat().guardar(this);
    }

}
