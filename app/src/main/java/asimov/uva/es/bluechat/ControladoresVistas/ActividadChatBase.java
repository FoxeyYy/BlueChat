package asimov.uva.es.bluechat.controladoresVistas;

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

import java.util.List;

import asimov.uva.es.bluechat.R;
import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Mensaje;

/**
 * Actividad base para los chats interactivos. Muestra los mensajes enviados y recibidos, y maneja
 * el acceso al almacenamiento externo para el envío de imágenes
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ActividadChatBase extends AppCompatActivity implements View.OnClickListener {

    /**
     * Resultado de la solicitud de acceso a imágenes
     */
    private final int READ_REQUEST_CODE = 0;

    /**
     * Resultado de la solicitud del permiso de localización
     */
    private final int PERMISO_ACCESO_DATOS = 1;

    /**
     * Chat a mostrar
     */
    private Chat chat;

    /**
     * Campo de texto para rellenar con el mensaje
     */
    private TextView campo_texto;

    /**
     * Layout de muestra de mensajes
     */
    private LinearLayout lista_mensajes;

    /**
     * URI interna de la imagen
     */
    private Uri uriImagen;


    /**
     * Devuelve un chat
     * @return chat El chat a devolver
     */
    Chat getChat() {
        return chat;
    }

    /**
     * Inicializa los valores para un chat
     * @param chat El chat con los valores para inicializar
     */
    void setChat(Chat chat) {
        this.chat = chat;
    }

    /**
     * Inicializa la vista de la lista de mensajes
     * @param lista La lista con los valores para inicializar
     */
    void setListaMensajes(LinearLayout lista) {
        lista_mensajes = lista;
    }

    /**
     * Inicializa los valores para la vista del campo de texto donde se mostrará el mensaje
     * @param vista La vista con los valores para inicializar
     */
    void setCampoTexto(TextView vista) {
        campo_texto = vista;
    }

    /**
     * Muestra los mensajes para un chat concreto, y desplaza la vista para mostrar el último
     */
    private final BroadcastReceiver receptorMensajes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Chat chat = intent.getParcelableExtra("chat");
            List<Mensaje> mensajes =  chat.getHistorial();

            if (getChat().equals(chat)) {
                mostrarMensajeRecibido(mensajes.get(mensajes.size()-1));
            }

            scrollAlUltimo();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uriImagen = data.getData();
            enviar();
        }

    }

    /**
     * Comprueba los permisos de acceso al almacenamiento externo para el empleo de imágenes
     */
    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ActividadChatBase.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                break;
            case R.id.boton_enviar:
                enviar();
                break;
            default:
                Log.e("Chat", "Boton incorrecto");
                break;
        }
    }

    /**
     * Construye los mensajes a enviar y solicita que se muestren en pantalla
     */
    protected void enviar() {
        String texto = String.valueOf(campo_texto.getText());
        Mensaje mensaje;
        if(texto.isEmpty() && uriImagen ==null)
            return;

        if (null == uriImagen) {
            mensaje = new Mensaje(getBaseContext(), texto);
        } else {
            mensaje = new Mensaje(getBaseContext(), texto, uriImagen);
        }

        mensaje.registrar(this, chat);
        if(uriImagen != null)
            mostrarMensajeEnviado(new Mensaje(getBaseContext(), texto,uriImagen));
        else
            mostrarMensajeEnviado(new Mensaje(getBaseContext(), texto));

        campo_texto.setText("");
        uriImagen = null;

        scrollAlUltimo();
    }


    /**
     * Desplaza la vista hacia abajo para mostrar el último mensaje intercambiado
     */
    void scrollAlUltimo() {
        final ScrollView scroll = (ScrollView) findViewById(R.id.scroll_chat);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    /**
     * Muestra al usuario las imágenes en el almacenamiento externo, y devuelve el resultado
     * de la seleccionada
     */
    private void buscarImagen(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Muestra el contenido de un mensaje recibido en la vista, así como el nombre del emisor
     * @param mensaje El mensaje a mostrar
     */
    void mostrarMensajeRecibido(Mensaje mensaje) {
        View tarjetaMensaje;

        if (null == mensaje.getImagen()) {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.msg_recibir, lista_mensajes, false);
        } else {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.mensaje_imagen_recibir, lista_mensajes, false);
            ImageView imageView = (ImageView) tarjetaMensaje.findViewById(R.id.imagen);
            imageView.setImageURI(Uri.parse(mensaje.getImagen()));
        }

        ((TextView) tarjetaMensaje.findViewById(R.id.texto_msg_recibir)).setText(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido() + "\n" + mensaje.getFecha());
        lista_mensajes.addView(tarjetaMensaje, lista_mensajes.getChildCount());
    }

    /**
     * Muestra el contenido de un mensaje enviado en la vista
     * @param mensaje El mensaje a mostrar
     */
    void mostrarMensajeEnviado(Mensaje mensaje) {
        View tarjetaMensaje;
        if (null == mensaje.getImagen()) {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.msg_enviar, lista_mensajes,false);
        } else {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.mensaje_imagen_enviar, lista_mensajes, false);
            ImageView imageView = (ImageView) tarjetaMensaje.findViewById(R.id.imagen);
            imageView.setImageURI(Uri.parse(mensaje.getImagen()));
        }

        ((TextView) tarjetaMensaje.findViewById(R.id.texto_msg_enviar)).setText(mensaje.getContenido()+ "\n" + mensaje.getFecha());
        lista_mensajes.addView(tarjetaMensaje, lista_mensajes.getChildCount());
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
