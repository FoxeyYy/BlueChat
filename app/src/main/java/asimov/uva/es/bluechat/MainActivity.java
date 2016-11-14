package asimov.uva.es.bluechat;

import android.Manifest;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static asimov.uva.es.bluechat.R.id.container;

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

    private LinearLayout lista;
    private Set<BluetoothDevice> conectados;
    private TabDescubrir tab_descubrir;

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
            buscarDispositivos();
            new ServidorBluetooth().start();
            conectados = adaptadorBluetooth.getBondedDevices();

        }

    }

    public void probarConexion(){
        for(BluetoothDevice device : conectados)
            if(device.getAddress().equals( "24:DA:9B:OC:83:E5") || device.getAddress().equals( "30:A8:DB:49:19:97")) {
                Log.d(TAG,"Enviando a dispositivo emparejado");
                new ClienteBluetooth(device).start();
            }
    }

    /**
     * Comprueba si la aplicacion posee los permisos necesarios para poder funcionar
     * De no ser así le pide dichos permisos al usuario
     */
    private void comprobarPermisos() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
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
        if (!adaptadorBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ACTIVADO);
        }
//        Intent discoverableIntent = new
//                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivityForResult(discoverableIntent,BLUETOOTH_VISIBLE);
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
        Log.d(TAG,"Buscando..");
        dispositivos.clear();
        // Creamos el objeto que va a recibir la notificacion cuando descubramos un nuevo dispositivo
        receptorBluetooth = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                // Descubrimos un nuevo dispositivo
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Obtenemos el nuevo dispostivo encontrado
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    tab_descubrir.anadirDispositivo(device);
                    dispositivos.add(dispositivos.size(),device);

                }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                    Log.d(TAG,"EMPEZANDO A DESCUBRIR");
                }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    Log.d(TAG,"TERMINANDO DESCUBRIMIENTO");
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
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receptorBluetooth);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case (R.id.action_settings):
                Intent intentAjustes= new Intent(this, AjustesActivity.class);
                startActivity(intentAjustes);
                probarConexion();
                break;

            case (R.id.action_bluetooth):
                Log.d(TAG,"Refrescar");
                if(esCompatibleBluetooth) {
                    activarBluetooth();
                    buscarDispositivos();
                    unregisterReceiver(receptorBluetooth);
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
                   return new Tab_chats();
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
