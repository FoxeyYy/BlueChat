package asimov.uva.es.bluechat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Mensaje;

/**
 * Actividad base para los chats interactivos
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ActividadChatBase extends AppCompatActivity implements View.OnClickListener {

    /**
     * Resultado de la solicitud de acceso a imagenes
     */
    private final int READ_REQUEST_CODE = 0;

    /**
     * Resultado de la solicitud del permiso de localización
     */
    private final int PERMISO_ACCESO_DATOS = 1;

    private BroadcastReceiver receptorMensajes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Mensaje mensaje = intent.getParcelableExtra("mensaje");
            Chat chat = intent.getParcelableExtra("chat");

            if (getChat().equals(chat)) {
                mostrarMensajeRecibido(mensaje);
            }
        }
    };

    /**
     * Chat a mostrar
     */
    private Chat chat;

    private TextView campo_texto;
    private LinearLayout lista_mensajes;

    private Uri uriImagen;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uriImagen = data.getData();
        }

    }

    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISO_ACCESO_DATOS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buscarImagen();

                } else {
                    //El usuario no proporciona permisos
                    //mostramos un mensaje indicando que son necesarios
                    Toast.makeText(this, R.string.permisos_imagen_denegados, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_foto:
                comprobarPermisos();
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

    protected void enviar() {
        String texto = String.valueOf(campo_texto.getText());
        if(!texto.isEmpty()) {
            Mensaje mensaje;

            if (null == uriImagen) {
                mensaje = new Mensaje(texto);
            } else {
                mensaje = new Mensaje(texto, uriImagen);
            }

            mensaje.registrar(this, chat);
            if (uriImagen != null)
                mostrarMensajeEnviado(new Mensaje(texto, uriImagen));
            else
                mostrarMensajeEnviado(new Mensaje(texto));

            campo_texto.setText("");
            uriImagen = null;

            scrollAlUltimo();
        }
    }

    protected void scrollAlUltimo() {
        final ScrollView scroll = (ScrollView) findViewById(R.id.scroll_chat);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void buscarImagen(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    protected void mostrarMensajeRecibido(Mensaje mensaje) {
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

    protected void mostrarMensajeEnviado(Mensaje mensaje) {
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

    protected Chat getChat () {
        return chat;
    }

    protected void setChat (Chat chat) {
        this.chat = chat;
    }

    protected void setListaMensajes (LinearLayout lista) {
        lista_mensajes = lista;
    }

    protected void setCampoTexto (TextView vista) {
        campo_texto = vista;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receptorMensajes);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter("mensajeNuevo");
        LocalBroadcastManager.getInstance(this).registerReceiver(receptorMensajes,filter);

        super.onResume();
    }
}
