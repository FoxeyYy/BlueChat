package asimov.uva.es.bluechat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;

/**
 * Actividad para los chats interactivos
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Resultado de la solicitud del permiso de localización
     */
    private final int PERMISO_ACCESO_DATOS = 1;

    /**
     * Chat a mostrar
     */
    private Chat chat;

    private TextView campo_texto;
    private LinearLayout lista_mensajes;

    private Uri uriImagen;

    /**
     * Resultado de la solicitud de acceso a imagenes
     */
    private final int READ_REQUEST_CODE = 1;

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
        findViewById(R.id.boton_foto).setOnClickListener(this);
        lista_mensajes = (LinearLayout) findViewById(R.id.lista_mensajes);
        campo_texto = (TextView) findViewById(R.id.texto);

        List<Mensaje> historial = chat.getHistorial();

        for(Mensaje msg: historial) {
            mostrarMensajeRecibido(msg);
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

    private void enviar() {
        String texto = String.valueOf(campo_texto.getText());
        Mensaje mensaje;

        if (null == uriImagen) {
            mensaje = new Mensaje(texto);
        } else {
            mensaje = new Mensaje(texto, uriImagen);
        }

        if (!chat.esPersistente()) {
            chat.guardar(getBaseContext());
        }

        Contacto contacto = chat.getPar();
        if (!contacto.esPersistente()) {
            contacto.guardar(getBaseContext());
        }

        mensaje.registrar(this, chat);
        mostrarMensajeEnviado(texto);

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

    private void mostrarMensajeRecibido(Mensaje mensaje) {
        View tarjetaMensaje;

        if (null == mensaje.getImagen()) {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.mensaje, null);
        } else {
            tarjetaMensaje = getLayoutInflater().inflate(R.layout.mensaje_imagen, null);
            ImageView imageView = (ImageView) tarjetaMensaje.findViewById(R.id.imagen);
            imageView.setImageURI(Uri.parse(mensaje.getImagen()));
        }

        ((TextView) tarjetaMensaje.findViewById(R.id.mensaje)).setText(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido());
        lista_mensajes.addView(tarjetaMensaje, lista_mensajes.getChildCount());
    }

    private void mostrarMensajeEnviado(String texto) {
        View mensaje;
        if (null == uriImagen) {
            mensaje = getLayoutInflater().inflate(R.layout.mensaje, null);
        } else {
            mensaje = getLayoutInflater().inflate(R.layout.mensaje_imagen, null);
            ImageView imageView = (ImageView) mensaje.findViewById(R.id.imagen);
            imageView.setImageURI(uriImagen);
        }

        ((TextView) mensaje.findViewById(R.id.mensaje)).setText(texto);
        lista_mensajes.addView(mensaje, lista_mensajes.getChildCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uriImagen = data.getData();
        }
    }

    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISO_ACCESO_DATOS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //El usuario nos proporciona permisos

                } else {
                    //El usuario no proporciona permisos
                    //mostramos un mensaje indicando que son necesarios
                    Toast.makeText(this, R.string.permisos_imagen_denegados, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
