package asimov.uva.es.bluechat;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

public class AjustesActivity extends AppCompatActivity {

    /**
     * Constantes para los ajustes
     */
    private static final String NOMBRE = "Nombre";
    private static final String AVATAR = "Avatar";

    /**
     * Ajustes de la app
     */
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preferencias);

        preferences = getPreferences(MODE_PRIVATE);
        mostrarPreferenciasGuardadas();
        mostrarAcercaDe();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Muestra la informacion acerca de al usuario
     */
    private void mostrarAcercaDe() {
        TextView acercaLabel = (TextView) findViewById(R.id.acerca_label);
        String autores = getString(R.string.autores);
        String version = "";
        try {
            PackageInfo pInfo = null;
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

        editor.commit();

    }

    private void mostrarPreferenciasGuardadas() {
        TextView nombre = (TextView)findViewById(R.id.nombre_preferencias);
        nombre.setText(getNombreGuardado());

        TextView mac = (TextView)findViewById(R.id.mac_preferencias);
        final String msg_mac = String.format(getString(R.string.mac_personal), getMacBluetooth());
        mac.setText(msg_mac);

        //TODO avatar
        ImageButton avatar = (ImageButton)findViewById(R.id.avatar_preferencias);
        //avatar.setBackground();
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

    private String getMacBluetooth() {
        return android.provider.Settings.Secure.getString(
                getBaseContext().getContentResolver(),
                "bluetooth_address"
        );
    }
}
