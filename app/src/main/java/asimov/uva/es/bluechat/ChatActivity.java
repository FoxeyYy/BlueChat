package asimov.uva.es.bluechat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Mensaje;
import asimov.uva.es.bluechat.sqllite.DBOperations;

/**
 * Actividad para los chats interactivos
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Chat a mostrar
     */
    private Chat chat;

    private TextView campo_texto;
    private LinearLayout lista_mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle params = getIntent().getExtras();
        chat = params.getParcelable("chat");

        ((TextView) findViewById(R.id.nombre_contacto)).setText(chat.getPar().getNombre());
        findViewById(R.id.boton_enviar).setOnClickListener(this);
        lista_mensajes = (LinearLayout) findViewById(R.id.lista_mensajes);
        campo_texto = (TextView) findViewById(R.id.texto);

        List<Mensaje> historial = chat.getHistorial();

        for(Mensaje msg: historial) {
            mostrarMensajeRecibido(msg.getContenido());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_enviar:
                String texto = String.valueOf(campo_texto.getText());
                Context contexto = this;
                Mensaje mensaje = new Mensaje(texto);
                mensaje.registrar(this, chat);
                mostrarMensajeEnviado(texto);
                campo_texto.setText("");
        }
    }

    private void mostrarMensajeRecibido(String texto) {
        View mensaje = getLayoutInflater().inflate(R.layout.mensaje, null);
        ((TextView) mensaje.findViewById(R.id.mensaje)).setText(chat.getPar().getNombre() + ": " + texto);
        lista_mensajes.addView(mensaje, lista_mensajes.getChildCount());
    }

    private void mostrarMensajeEnviado(String texto) {
        View mensaje = getLayoutInflater().inflate(R.layout.mensaje, null);
        ((TextView) mensaje.findViewById(R.id.mensaje)).setText(texto);
        lista_mensajes.addView(mensaje, lista_mensajes.getChildCount());
    }
}
