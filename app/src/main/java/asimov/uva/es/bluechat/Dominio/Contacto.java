package asimov.uva.es.bluechat.Dominio;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import asimov.uva.es.bluechat.sqllite.DBContract;
import asimov.uva.es.bluechat.sqllite.DBOperations;

/**
 * Contacto de la App,
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
    private final String imagen;

    /**
     * Encuentra un contacto
     * @param context
     * @param mac
     * @return el contacto o null si no existe
     */
    public static Contacto getContacto(Context context, String mac) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getContact(mac);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();

        String nombre = cursor.getString(cursor.getColumnIndex(DBContract.Contacto.COLUMN_NAME_NOMBRE));
        String imagen = cursor.getString(cursor.getColumnIndex(DBContract.Contacto.COLUMN_NAME_IMAGE));

        Contacto contacto = new Contacto(nombre, mac, imagen);

        cursor.close();
        return contacto;

    }

    /**
     * Guarda el contacto
     * @param context de la actividad
     */
    public void guardar(Context context)  {
        DBOperations.obtenerInstancia(context).insertContact(this);
    }

    /**
     * Inicializa a los parámetros que se indican
     * @param direccionMac La MAC del usuario
     * @param nombre Nombre del usuario
     * @param imagen Imagen del usuario
     */
    public Contacto (String nombre, String direccionMac, String imagen) {
        this.direccionMac = direccionMac;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    /**
     * Constructor parceable
     * @param in datos del parceable
     */
    private Contacto(Parcel in) {
        nombre = in.readString();
        direccionMac = in.readString();
        imagen = in.readString();
    }

    /**
     * Constructor parceable
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
     * Método que permite a las clases que hereden de ésta identificar sus contenidos parcelables
     * @return 0 El valor para la clase original
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcela los atributos del contacto, almacenándolos en la estructura destino
     * @param dest La estructura destino de almacenamiento
     * @param flags El número de flag necesario para efectuar la operación
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(direccionMac);
        dest.writeString(imagen);
    }

}
