package asimov.uva.es.bluechat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static asimov.uva.es.bluechat.R.id.container;

/**
 * Actividad principal de la aplicacion
 * Encargada de comprobar la disponibilidad del bluetooth,
 * pedir los permisos pertinentes y activar el mismo.
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class MainActivity extends AppCompatActivity{

    /**
     * Resultado de la solicitud del permiso de localizacion
     */
    private final int PERMISO_LOCALIZACION = 1;

    /**
     * Resultado de la solicitud de la activacion del bluetooth
     */
    private final int BLUETOOTH_ACTIVADO = 1;

    /**
     * Resultado de la solicitud de visibilidad del bluetooth
     */
    private final int BLUETOOTH_VISIBLE = 1;

    /**
     * Adaptador bluetooth del dispositivo
     */
    private BluetoothAdapter adaptadorBluetooth;

    /**
     * Receptor de informacion de los dispositivos descubiertos
     */
    private BroadcastReceiver receptorBluetooth;

    /**
     * Estado del dispositivo bluetooth, buscando o no
     */
    private boolean buscando = false;

    /**
     * El dispositivo es compatible con el bluetooth
     */
    private boolean esCompatibleBluetooth;

    /**
     * Tag para debug
     */
    public static final String TAG = "BLUETOOTH";

    /**
     * Dispositivos descubiertos
     */
    private static List<BluetoothDevice> dispositivos;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * Tab que muestra los dispositivos encontrados mediante bluetooth
     */
    private TabDescubrir tab_descubrir;

    /**
     * Implementacion para el patron Singleton
     */
    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "EMPEZANDO MAIN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        comprobarBluetooth();

        if( esCompatibleBluetooth ) {
            dispositivos = new ArrayList<BluetoothDevice>();
            comprobarPermisos();
            activarBluetooth();
        }

        mainActivity = this;

    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    /**
     * Muestra una notificacion con el mensaje recibido como parametro
     * @param mensaje que mostrar en la notificacion
     */
    public void notificar(String mensaje){
        Intent intent = new Intent(this, NotificationCompat.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notificacion =
                new NotificationCompat.Builder(this)
                        .setContentTitle("BlueChat")
                        .setSmallIcon(R.drawable.noticacion_icon)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setFullScreenIntent(pIntent,true)
                        .setContentText(mensaje).build();

        manager.notify(0,notificacion);


    }



    /**
     * Comprueba si la aplicacion posee los permisos necesarios para poder funcionar
     * De no ser así le pide dichos permisos al usuario
     */
    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LOCALIZACION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //El usuario nos proporciona permisos

                } else {
                    //El usuario no proporciona permisos
                    //mostramos un mensaje indicando que son necesarios
                    Toast.makeText(this, R.string.permisos_denegados, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    /**
     * Comprueba si el disposivo dispone de bluetooth
     */
    private void comprobarBluetooth() {
        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();
        esCompatibleBluetooth = true;
        if (adaptadorBluetooth == null) {
            Log.d(TAG, "BLUETOOTH NO DISPONIBLE");
            esCompatibleBluetooth = false;
            Toast.makeText(this, R.string.dispositivo_sin_bluetooth, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Pide permiso al usuario para activar el Bluetooth del dispositivo
     */
    private void activarBluetooth(){

        if( BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE != adaptadorBluetooth.getScanMode()) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(discoverableIntent, BLUETOOTH_VISIBLE);
        }

        if (!adaptadorBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ACTIVADO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_ACTIVADO) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.info_bluetooth_desactivado, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == BLUETOOTH_VISIBLE){
            if(resultCode == RESULT_CANCELED)
                Toast.makeText(this, R.string.info_bluetooth_visible, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Busca los dispositivos que se encuentren en modo visible dentro del rango
     */
    private void buscarDispositivos() {
        //Elimina los dispositivos encontrados previamente
        //asi como las tarjetas asociadas a los mismos en la vista
        dispositivos.clear();
        tab_descubrir.eliminarTarjetas();

        //Muestra una barra de progreso al usuario
        ProgressBar barraProgreso = (ProgressBar) findViewById(R.id.bar_descubrir);
        barraProgreso.setVisibility(View.VISIBLE);

        // Creamos el objeto que va a recibir la notificacion cuando descubramos un nuevo dispositivo
        receptorBluetooth = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                // Descubrimos un nuevo dispositivo
                switch (action) {
                    case (BluetoothDevice.ACTION_FOUND):

                        // Obtenemos el nuevo dispostivo encontrado
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (!dispositivos.contains(device)) {
                            Log.d(TAG, "Descubierto dispositivo " + device.getAddress());
                            tab_descubrir.anadirDispositivo(device);
                            dispositivos.add(device);
                        }
                        break;

                    //
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        Log.d(TAG, "EMPEZANDO A DESCUBRIR");
                        buscando = true;
                        break;

                    //Finaliza el descubrimiento, oculta la barra de progreso
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        Log.d(TAG, "TERMINANDO DESCUBRIMIENTO");
                        buscando = false;
                        ProgressBar barraProgreso = (ProgressBar) findViewById(R.id.bar_descubrir);
                        barraProgreso.setVisibility(View.INVISIBLE);
                        unregisterReceiver(receptorBluetooth);
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receptorBluetooth, filter);
        adaptadorBluetooth.startDiscovery();
    }

    @Override
    protected void onPause(){
        super.onPause();
        adaptadorBluetooth.cancelDiscovery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case (R.id.action_settings):
                Intent intentAjustes= new Intent(this, AjustesActivity.class);
                startActivity(intentAjustes);
                break;

            case (R.id.action_bluetooth):
                Log.d(TAG,"Refrescar");
                if(esCompatibleBluetooth) {
                    activarBluetooth();
                    if (!buscando) {
                        buscarDispositivos();
                        new ServidorBluetooth().start();
                    }
                }else
                    Toast.makeText(this,
                                    R.string.dispositivo_sin_bluetooth,
                                    Toast.LENGTH_SHORT).show();
                break;

            default:
                Log.e(TAG, "Elemento de menu desconocido");
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * Numero de tabs a mostrar
         */
        private static final int NUM_TABS = 2;

        /**
         * Crea un nuevo {@link SectionsPagerAdapter}
         * @param fm el fragment manager
         */
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
           switch (position){
               case 0:
                   tab_descubrir = new TabDescubrir();
                   return tab_descubrir;
               case 1:
                   return new TabChats();
               default:
                   Log.d(TAG,"Error seleccionando tag");
                   return null;
           }
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_descubrir);
                case 1:
                    return getString(R.string.tab_chats);
            }
            return null;
        }
    }
}
