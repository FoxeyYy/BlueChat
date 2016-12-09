package asimov.uva.es.bluechat.Dominio;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import asimov.uva.es.bluechat.sqllite.DBContract;
import asimov.uva.es.bluechat.sqllite.DBOperations;

/**
 * Mensaje enviado por la App,
 * contiene tanto el mensaje como los metadatos.
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */

public class Mensaje implements Parcelable{

    private String contenido;
    private Contacto emisor;
    private Date fecha;
    private int estado;

    public static List<Mensaje> getMensajes(Context context, String chat) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getAllMessages();
        List<Mensaje> mensajes = new ArrayList();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String contenido = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_CONTENT));
            Contacto emisor = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_EMISOR)));
            String fecha = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_FECHA));

            mensajes.add(new Mensaje(contenido, emisor, new Date())); //TODO Fecha de la bbdd
        }

        cursor.close();

        return mensajes;
    }

    /**
     * Guarda el mensaje
     * @param context de la actividad
     * @param chat del mensaje
     */
    public void guardar(Context context, Chat chat)  {
        DBOperations.obtenerInstancia(context).insertMessage(this, chat);
    }

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     * @param fecha fecha del mensaje
     */
    public Mensaje(String contenido, Contacto emisor, Date fecha) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.fecha = fecha;
        this.estado = 0;
    }

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     */
    public Mensaje(String contenido, Contacto emisor) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.fecha = new Date();
        this.estado = 0;
    }

    protected Mensaje(Parcel in) {
        contenido = in.readString();
        emisor = in.readParcelable(Contacto.class.getClassLoader());
        fecha = new Date(in.readLong());
    }

    public static final Creator<Mensaje> CREATOR = new Creator<Mensaje>() {
        @Override
        public Mensaje createFromParcel(Parcel in) {
            return new Mensaje(in);
        }

        @Override
        public Mensaje[] newArray(int size) {
            return new Mensaje[size];
        }
    };

    /**
     * Obtiene el contenido del mensaje
     * @return contenido El contenido del mensaje
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Obtiene el nombre del emisor del mensaje
     * @return emisor El emisor del mensaje
     */
    public Contacto getEmisor() {
        return emisor;
    }

    /**
     * Obtiene la fecha de envío del mensaje
     * @return fecha La fecha de envío del mensaje
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Obtiene el estado del mensaje
     * @return estado El estado del mensaje
     */
    public int getEstado() {
        return estado;
    }

    /**
     * Modifica el valor del estado del mensaje
     * @param estado El estado del mensaje
     */
    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contenido);
        dest.writeParcelable(emisor, flags);
        dest.writeLong(fecha.getTime());
    }
}
