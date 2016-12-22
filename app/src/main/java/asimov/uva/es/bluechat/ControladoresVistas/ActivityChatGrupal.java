package asimov.uva.es.bluechat.ControladoresVistas;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;
import asimov.uva.es.bluechat.R;

public class ActivityChatGrupal extends ActividadChatBase {

    public static final String CONTACTOS = "Participantes";
    public static final String NOMBRE_GRUPO = "Nombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_grupal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle params = getIntent().getExtras();
        Chat chat = Chat.getChatGrupal(this, params.getString("idChat"));
        setChat(chat);

        if (null == getChat()) {
            nuevoChat(params);
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

        Contacto myself = Contacto.getSelf();
        for(Mensaje msg: historial) {
            if(msg.getEmisor().equals(myself))
                mostrarMensajeEnviado(msg);
            else
                mostrarMensajeRecibido(msg);
        }

        scrollAlUltimo();

    }

    private void nuevoChat (Bundle params) {
        List<Contacto> participantes = params.getParcelableArrayList(CONTACTOS);
        String nombreGrupo = params.getString(NOMBRE_GRUPO);

        setChat(new Chat(nombreGrupo, participantes));
        getChat().guardar(this);
    }

}
