package asimov.uva.es.bluechat.ControladoresVistas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Actividad de entrada de la app, se encarga de redirigir al usuario a la actividad correspondiente
 * dependiendo de si es la primera vez que inicia la app o no
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ActivityEntrada extends AppCompatActivity {

    /**
     * Constante identificativa del ajuste para detectar el primer uso de la App
     */
    public static final String AJUSTE_PRIMERA_VEZ = "primeraVez";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferencias = getSharedPreferences(ActivityAjustes.PREFERENCIAS, MODE_PRIVATE);
        boolean primeraVez = preferencias.getBoolean(AJUSTE_PRIMERA_VEZ, true);

        Intent intent;
        if(primeraVez){
            intent = new Intent(this, ActivityPrimeraVez.class);
        } else {
            intent = new Intent(this, ActivityPrincipal.class);
        }

        startActivity(intent);
        finish();

    }
}
