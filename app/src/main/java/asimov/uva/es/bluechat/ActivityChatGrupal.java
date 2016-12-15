package asimov.uva.es.bluechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;

public class ActivityChatGrupal extends AppCompatActivity implements View.OnClickListener {

    public static final String CONTACTOS = "Participantes";
    public static final String NOMBRE_GRUPO = "Nombre";

    private List<Contacto> participantes;

    private LinearLayout lista_mensajes;
    private TextView campo_texto;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_grupal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle params = getIntent().getExtras();
        participantes = params.getParcelableArrayList(CONTACTOS);
        String nombreGrupo = params.getString(NOMBRE_GRUPO);
        String nombres = "";

        for (Contacto contacto : participantes) {
            nombres += contacto.getNombre()+ ", ";
        }

        nombres = nombres.substring(0, nombres.length() - 2);

        ((TextView) findViewById(R.id.nombre_grupo)).setText(nombreGrupo);
        ((TextView) findViewById(R.id.participantes)).setText(nombres);

        findViewById(R.id.boton_enviar).setOnClickListener(this);
        findViewById(R.id.boton_foto).setOnClickListener(this);

        lista_mensajes = (LinearLayout) findViewById(R.id.lista_mensajes);
        campo_texto = (TextView) findViewById(R.id.texto);

        chat = new Chat(nombreGrupo, participantes);
        chat.guardar(this);
    }

    @Override
    public void onClick(View v) {

    }
}
