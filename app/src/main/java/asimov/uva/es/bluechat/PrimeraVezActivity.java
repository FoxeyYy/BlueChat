package asimov.uva.es.bluechat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class PrimeraVezActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Direccion de la imagen de perfil
     */
    private String avatar;

    /**
     * Resultado de la solicitud del permiso de localización
     */
    private final int PERMISO_ACCESO_DATOS = 1;

    /**
     * Resultado de la solicitud de acceso a imagenes
     */
    private final int READ_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_primera_vez);

        Button infoSiguiente = (Button) findViewById(R.id.boton_info_siguiente);
        infoSiguiente.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.boton_info_siguiente:
                setContentView(R.layout.activity_primera_vez);
                siguientePantalla();
                break;
            case R.id.boton_siguiente:
                if(apodoVacio()){
                    Toast.makeText(this, "El campo de apodo es obligatorio", Toast.LENGTH_LONG).show();
                }else {
                    guardarPreferencias();
                    finish();
                }
                break;
            case R.id.selecciona_imagen:
                comprobarPermisos();
                buscarImagen();
                break;
        }
    }


    private void siguientePantalla(){
        final Button siguiente = (Button) findViewById(R.id.boton_siguiente);
        Button seleccionImagen = (Button) findViewById(R.id.selecciona_imagen);
        siguiente.setOnClickListener(this);
        seleccionImagen.setOnClickListener(this);

    }

    /**
     * Guarda los valores en las preferencias
     */
    private void guardarPreferencias(){
        SharedPreferences preferencias = getSharedPreferences(AjustesActivity.PREFERENCIAS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        TextView textNombre = (TextView) findViewById(R.id.apodo);
        String nombre = String.valueOf(textNombre.getText());

        //TODO direccion del icono
        if(avatar == null)
            avatar = "android.resource://asimov.uva.es.bluechat/mipmap/ic_launcher";

        editor.putString(AjustesActivity.NOMBRE, nombre);
        editor.putString(AjustesActivity.AVATAR, avatar);
        editor.commit();
    }

    private void buscarImagen(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ImageView imagenPerfil = (ImageView) findViewById(R.id.imagen_perfil);
            avatar = uri.toString();
            imagenPerfil.setImageURI(uri);
        }
    }

    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(PrimeraVezActivity.this,
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

    private boolean apodoVacio(){
        TextView campoApodo = (TextView)findViewById(R.id.apodo);
        String apodo = String.valueOf(campoApodo.getText());
        return apodo.length() == 0;
    }


}
