package asimov.uva.es.bluechat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class AjustesActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Resultado de la solicitud del permiso de localización
     */
    private final int PERMISO_ACCESO_DATOS = 1;

    /**
     * Constantes para los ajustes
     */
    public static final String NOMBRE = "Nombre";
    public static final String AVATAR = "Avatar";
    public static final String PREFERENCIAS = "Preferencias";

    /**
     * Resultado de la solicitud de acceso a imagenes
     */
    private final int READ_REQUEST_CODE = 1;

    /**
     * Ajustes de la app
     */
    private SharedPreferences preferences;

    /**
     * Direccion de la imagen de perfil
     */
    private Uri uriImagen;

    private final String IMAGEN = "Imagen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preferencias);

        ImageButton boton = (ImageButton) findViewById(R.id.avatar_preferencias);
        boton.setOnClickListener(this);

        preferences = getSharedPreferences(PREFERENCIAS, Activity.MODE_PRIVATE);
        mostrarPreferenciasGuardadas();
        mostrarAcercaDe();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Muestra la información acerca de al usuario
     */
    private void mostrarAcercaDe() {
        TextView acercaLabel = (TextView) findViewById(R.id.acerca_label);
        String autores = getString(R.string.autores);
        String version;
        try {
            PackageInfo pInfo;
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = getString(android.R.string.unknownName);
        }
        String mensajeAcerca = getString(R.string.acerca_de, autores, version);
        acercaLabel.setText(mensajeAcerca);
    }

    @Override
    protected void onStop() {

        super.onStop();

        SharedPreferences.Editor editor = preferences.edit();

        TextView nombre = (TextView)findViewById(R.id.nombre_preferencias);
        editor.putString(NOMBRE, String.valueOf(nombre.getText()));
        if(uriImagen != null)
            editor.putString(AVATAR, uriImagen.toString());

        editor.commit();

    }

    /**
     * Muetra las preferencias previamente guardadas
     */
    private void mostrarPreferenciasGuardadas() {
        TextView nombre = (TextView)findViewById(R.id.nombre_preferencias);
        nombre.setText(getNombreGuardado());

        TextView mac = (TextView)findViewById(R.id.mac_preferencias);
        final String msg_mac = String.format(getString(R.string.mac_personal), getMacBluetooth());
        mac.setText(msg_mac);

        ImageButton avatar = (ImageButton) findViewById(R.id.avatar_preferencias);
        String avatarContacto = getAvatar();
        if(avatarContacto != null && !avatarContacto.isEmpty()) {
            Uri uri = Uri.parse(avatarContacto);
            avatar.setImageURI(uri);
        }else{
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            avatar.setImageBitmap(image);
        }

    }

    /**
     * Devuelve el nombre guardado
     * @return el nombre a mostrar para el usuario
     */
    private String getNombreGuardado() {
        return preferences.getString(NOMBRE, "");
    }

    /**
     * Devuelve el path al avatar del usuario
     * @return el path del avatar a mostrar
     */
    private String getAvatar() {
        return preferences.getString(AVATAR, "");
    }

    /**
     * Devuelve la dirección MAC del dispositivo
     * @return la direcció MAC del dispositivo
     */
    private String getMacBluetooth() {
        return android.provider.Settings.Secure.getString(
                getBaseContext().getContentResolver(),
                "bluetooth_address"
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.avatar_preferencias:
                comprobarPermisos();
        }
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
            Log.d(IMAGEN, "Uri: " + uri.toString());
            ImageButton imagenPerfil = (ImageButton) findViewById(R.id.avatar_preferencias);
            uriImagen = uri;
            imagenPerfil.setImageURI(uri);

        }
    }

    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(AjustesActivity.this,
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
}
