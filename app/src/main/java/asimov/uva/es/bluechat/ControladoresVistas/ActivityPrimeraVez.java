package asimov.uva.es.bluechat.controladoresVistas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import asimov.uva.es.bluechat.R;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ActivityPrimeraVez extends AppCompatActivity implements View.OnClickListener {

    /**
     * Direccion de la imagen de perfil
     */
    private String avatar;

    /**
     * Resultado de la solicitud del permiso de acceso a datos
     */
    private final int PERMISO_ACCESO_DATOS = 1;


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
                    Toast.makeText(this, R.string.error_apodo_primera_vez, Toast.LENGTH_LONG).show();
                }else {
                    comprobarPermisos();
                    guardarPreferencias();
                    finalizarPrimeraVez();
                    Intent intent = new Intent(this, ActivityPrincipal.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.selecciona_imagen:
                comprobarPermisosImagen();
                break;
        }
    }


    /**
     * Permite al usuario navegar a la siguiente pantalla inicial
     */
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
        SharedPreferences preferencias = getSharedPreferences(ActivityAjustes.PREFERENCIAS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        TextView textNombre = (TextView) findViewById(R.id.apodo);
        String nombre = String.valueOf(textNombre.getText());

        editor.putString(ActivityAjustes.NOMBRE, nombre);
        editor.putString(ActivityAjustes.AVATAR, avatar);
        editor.apply();
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
        startActivityForResult(intent, PERMISO_ACCESO_DATOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISO_ACCESO_DATOS && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ImageView imagenPerfil = (ImageView) findViewById(R.id.imagen_perfil);
            avatar = uri.toString();
            imagenPerfil.setImageURI(uri);
        }
    }

    /**
     * Comprueba los permisos de acceso al almacenamiento externo para el empleo de imágenes
     */
    private void comprobarPermisosImagen() {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(ActivityPrimeraVez.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO_ACCESO_DATOS);

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


    /**
     * Comprueba si el campo de apodo en la vista está vacío
     * @return la longitud del apodo
     */
    private boolean apodoVacio(){
        TextView campoApodo = (TextView)findViewById(R.id.apodo);
        String apodo = String.valueOf(campoApodo.getText());
        return apodo.length() == 0;
    }

    /**
     * Una vez cumplimentados todos los pasos correctamente, se bloquea esta actividad para que no
     * vuelva a ser mostrada
     */
    private void finalizarPrimeraVez(){
        SharedPreferences preferencias = getSharedPreferences(ActivityAjustes.PREFERENCIAS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean(ActivityEntrada.AJUSTE_PRIMERA_VEZ, false);
        editor.apply();
    }

    /**
     * Comprueba todos los permisos necesarios para el funcionamiento de la aplicación: permisos de
     * acceso al almacenamiento externo, permisos de acceso a contactos y permiso de localización,
     * necesario en últimas versiones de Android.
     */
    private void comprobarPermisos(){

        ArrayList<String> permisosNecesarios = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permisosNecesarios.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            permisosNecesarios.add(Manifest.permission.READ_CONTACTS);
        }

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            permisosNecesarios.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permisosNecesarios.isEmpty()) {
            String[] arraySolicitudes = new String[permisosNecesarios.size()];
            arraySolicitudes = permisosNecesarios.toArray(arraySolicitudes);
            ActivityCompat.requestPermissions(ActivityPrimeraVez.this,
                    arraySolicitudes, PERMISO_ACCESO_DATOS);
        }

    }
}
