package asimov.uva.es.bluechat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;

public class ActivityChatGrupal extends AppCompatActivity implements View.OnClickListener {

    public static final String CONTACTOS = "Participantes";
    public static final String NOMBRE_GRUPO = "Nombre";

    /**
     * Resultado de la solicitud de acceso a imagenes
     */
    private final int READ_REQUEST_CODE = 1;

    private Uri uriImagen;

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
        chat = params.getParcelable("chat");

        if (null == chat) {
            nuevoChat(params);
        }

        findViewById(R.id.boton_enviar).setOnClickListener(this);
        findViewById(R.id.boton_foto).setOnClickListener(this);

        lista_mensajes = (LinearLayout) findViewById(R.id.lista_mensajes);
        campo_texto = (TextView) findViewById(R.id.texto);

        String nombres = "";

        for (Contacto contacto : chat.getParticipantes()) {
            nombres += contacto.getNombre()+ ", ";
        }

        nombres = nombres.substring(0, nombres.length() - 2);
        ((TextView) findViewById(R.id.nombre_grupo)).setText(chat.getNombre());
        ((TextView) findViewById(R.id.participantes)).setText(nombres);

        List<Mensaje> historial = chat.getHistorial();

        Contacto myself = Contacto.getSelf();
        for(Mensaje msg: historial) {
            if(msg.getEmisor().equals(myself))
                mostrarMensajeEnviado(msg);
            else
                mostrarMensajeRecibido(msg);
        }

    }

    private void mostrarMensajeRecibido(Mensaje mensaje) {
        View tarjetaMensaje;

        if (null == mensaje.getImagen()) {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.msg_recibir, null);
        } else {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.mensaje_imagen_recibir, null);
            ImageView imageView = (ImageView) tarjetaMensaje.findViewById(R.id.imagen);
            imageView.setImageURI(Uri.parse(mensaje.getImagen()));
        }

        ((TextView) tarjetaMensaje.findViewById(R.id.texto_msg_recibir)).setText(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido());
        lista_mensajes.addView(tarjetaMensaje, lista_mensajes.getChildCount());
    }

    private void nuevoChat (Bundle params) {
        List<Contacto> participantes = params.getParcelableArrayList(CONTACTOS);
        String nombreGrupo = params.getString(NOMBRE_GRUPO);

        chat = new Chat(nombreGrupo, participantes);
        chat.guardar(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_foto:
                buscarImagen();
                break;
            case R.id.boton_enviar:
                enviar();
                break;
            default:
                Log.e("Chat", "Boton incorrecto");
                break;
        }
    }

    private void enviar() {
        String texto = String.valueOf(campo_texto.getText());
        Mensaje mensaje;

        if (null == uriImagen) {
            mensaje = new Mensaje(texto);
        } else {
            mensaje = new Mensaje(texto, uriImagen);
        }

        mensaje.registrar(this, chat);
        mostrarMensajeEnviado(mensaje);

        campo_texto.setText("");
        uriImagen = null;
    }

    private void buscarImagen(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void mostrarMensajeEnviado(Mensaje mensaje) {
        View tarjetaMensaje;
        if (null == mensaje.getImagen()) {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.msg_enviar, null);
        } else {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.mensaje_imagen_enviar, null);
            ImageView imageView = (ImageView) tarjetaMensaje.findViewById(R.id.imagen);
            imageView.setImageURI(Uri.parse(mensaje.getImagen()));
        }

        ((TextView) tarjetaMensaje.findViewById(R.id.texto_msg_enviar)).setText(mensaje.getContenido());
        lista_mensajes.addView(tarjetaMensaje, lista_mensajes.getChildCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uriImagen = data.getData();
        }
    }
}
