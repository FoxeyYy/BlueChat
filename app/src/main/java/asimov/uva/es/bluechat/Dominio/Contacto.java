package asimov.uva.es.bluechat.dominio;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import asimov.uva.es.bluechat.controladoresVistas.ActivityAjustes;
import asimov.uva.es.bluechat.controladoresVistas.ActivityPrincipal;
import asimov.uva.es.bluechat.persistencia.DBContract;
import asimov.uva.es.bluechat.persistencia.DBOperations;

/**
 * Representa un contacto de la App,
 * contiene datos identificativos para una persona
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class Contacto implements Parcelable, Serializable{

    /**
     * Nombre del contacto
     */
    private final String nombre;

    /**
     * Direcion mac asociado al contacto
     */
    private final String direccionMac;

    /**
     * Ruta de la imagen asociada al contacto
    */
    private String imagen;

    /**
     * Indicador de persistencia
     */
    private boolean esPersistente;

    /**
     * Devuelve el propio contacto
     * @return contacto propio
     */
    public static Contacto getSelf() {
        String nombre = ActivityPrincipal.getActivityPrincipal().getSharedPreferences(ActivityAjustes.PREFERENCIAS, Activity.MODE_PRIVATE).getString(ActivityAjustes.NOMBRE, "");
        String avatar = ActivityPrincipal.getActivityPrincipal().getSharedPreferences(ActivityAjustes.PREFERENCIAS, Activity.MODE_PRIVATE).getString(ActivityAjustes.AVATAR, "");
        String mac = android.provider.Settings.Secure.getString(ActivityPrincipal.getActivityPrincipal().getContentResolver(),"bluetooth_address");
        return new Contacto (nombre, mac, avatar, true);
    }

    /**
     * Devuelve una lista con todos los contactos conocidos
     * @param contexto El contexto de acceso a persistencia
     * @return contactos La lista de contactos conocidos
     */
    public static List<Contacto> getContactos (Context contexto) {
        List<Contacto> contactos = new ArrayList();

        Cursor cursor = DBOperations.obtenerInstancia(contexto).getAllContacts();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String mac = cursor.getString(cursor.getColumnIndex(DBContract.Contacto.COLUMN_NAME_MAC));
            Contacto contacto = getContacto(contexto, mac);
            contactos.add(contacto);
        }

        cursor.close();

        return contactos;
    }

    /**
     * Encuentra un contacto
     * @param context El contexto de acceso a persistencia
     * @param device El dispositivo a encontrar
     * @return contacto El contacto o null si no existe
     */
    public static Contacto getContacto(Context context, BluetoothDevice device) {
        Contacto contacto = getContacto(context, device.getAddress());

        if (null == contacto) {
            contacto = new Contacto(device.getName(), device.getAddress(), "", false);
        }

        return contacto;

    }

    /**
     * Encuentra un contacto
     * @param context El contexto de acceso a persistencia
     * @param mac La MAC a encontrar
     * @return contacto El contacto o null si no existe
     */
    public static Contacto getContacto(Context context, String mac) {

        if (!BluetoothAdapter.checkBluetoothAddress(mac)) {
            return null;
        }

        if (mac.equals(android.provider.Settings.Secure.getString(context.getContentResolver(),"bluetooth_address"))) {
            return getSelf();
        }

        Cursor cursor = DBOperations.obtenerInstancia(context).getContact(mac);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        String nombre = getNombreContacto(context, mac);

        if (null == nombre) {
            nombre = cursor.getString(cursor.getColumnIndex(DBContract.Contacto.COLUMN_NAME_NOMBRE));
        }

        String imagen = cursor.getString(cursor.getColumnIndex(DBContract.Contacto.COLUMN_NAME_IMAGE));

        Contacto contacto = new Contacto(nombre, mac, imagen, true);

        cursor.close();
        return contacto;

    }

    /**
     * Encuentra los participantes de un grupo
     * @param contexto El contexto de acceso a persistencia
     * @param idGrupo El identificador de grupo a buscar
     * @return participantes La lista de participantes
     */
    public static List<Contacto> getParticipantesGrupo(Context contexto, String idGrupo) {
        List<Contacto> participantes = new ArrayList();
        Cursor cursor = DBOperations.obtenerInstancia(contexto).getParticipantesGrupo(idGrupo);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idContacto = cursor.getString(cursor.getColumnIndex(DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CONTACTO));
            Contacto participante = getContacto(contexto, idContacto);
            participantes.add(participante);
        }

        return participantes;
    }

    /**
     * Devuelve los participantes de un chat que tengan mensajes pendientes
     * @param contexto El contexto de acceso a persistencia
     * @param idGrupo El identificador de grupo
     * @return participantes La lista de participantes
     */
    public static List<Contacto> getParticipantesConMensajesPendientes(Context contexto, String idGrupo){
        List<Contacto> participantes = new ArrayList();
        Cursor cursor = DBOperations.obtenerInstancia(contexto).getParticipantesConMensajesPendientes(idGrupo);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idContacto = cursor.getString(cursor.getColumnIndex(DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CONTACTO));
            Contacto participante = getContacto(contexto, idContacto);
            participantes.add(participante);
        }

        return participantes;
    }

    /**
     * Devuelve el nombre de un contacto si ya ha sido registrado en la agenda.
     * @param context de accesso a persistencia
     * @param mac del contacto
     * @return nombre de la agenda del contacto, null si no esta asociado.
     */
    private static String getNombreContacto(Context context, String mac) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            return null;
        }

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mac));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            cursor.close();
            return nombre;
        }

        cursor.close();
        return null;
    }
    /**
     * Devuelve el chat de un contacto
     * @return chat El chat si existe y si no, null
     */
    public Chat getChat(Context contexto) {
        List<Chat> chats = Chat.getChats(contexto);
        for(Chat chat : chats){
            if(null != chat.getPar() && chat.getPar().equals(this) && !chat.esGrupo()){
                return chat;
            }
        }
        return null;
    }

    /**
     * Guarda el contacto
     * @param context El contexto de la actividad
     */
    public void guardar(Context context)  {
        DBOperations.obtenerInstancia(context).insertContact(this);
        esPersistente = true;
    }

    /**
     * Inicializa a los parámetros que se indican
     * @param direccionMac La MAC del usuario
     * @param nombre El nombre del usuario
     * @param imagen La imagen del usuario
     */
    private Contacto (String nombre, String direccionMac, String imagen, boolean persistente) {
        this.direccionMac = direccionMac;
        this.nombre = nombre;
        this.imagen = imagen;
        esPersistente = persistente;
    }

    /**
     * Constructor parceable
     * @param in Los datos del parcelable
     */
    private Contacto(Parcel in) {
        nombre = in.readString();
        direccionMac = in.readString();
        imagen = in.readString();
        esPersistente = in.readByte() != 0;
    }

    /**
     * Constructor del objeto parcelable
     */
    public static final Creator<Contacto> CREATOR = new Creator<Contacto>() {
        @Override
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        @Override
        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };

    /**
     * Obtiene la dirección MAC del contacto
     * @return direccionMac La dirección MAC del contacto
     */
    public String getDireccionMac() {
        return direccionMac;
    }

    /**
     * Obtiene el nombre del contacto
     * @return nombre El nombre del contacto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la ruta a la imagen del contacto
     * @return imagen La ruta a la imagen del contacto
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Indica si es persistente
     * @return true si es persistente, false en otro caso
     */
    public boolean esPersistente() {
        return esPersistente;
    }

    /**
     * Establece un valor para la imagen
     * @param imagen La imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /**
     * Permite a las clases que hereden de ésta identificar sus contenidos parcelables
     * @return 0 El valor para la clase original
     */
    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(direccionMac);
        dest.writeString(imagen);
        dest.writeInt((byte) (esPersistente ? 1 : 0));
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return getDireccionMac().equals(contacto.getDireccionMac());

    }


}
