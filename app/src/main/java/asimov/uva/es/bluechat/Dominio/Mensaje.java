package asimov.uva.es.bluechat.Dominio;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import asimov.uva.es.bluechat.MainActivity;
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

public class Mensaje implements Parcelable, Serializable {

    private String id;
    private String contenido;
    private Contacto emisor;
    private Date fecha;
    private int estado;

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     * @param fecha fecha del mensaje
     */
    public Mensaje(String id, String contenido, Contacto emisor, Date fecha) {
        this.id = id;
        this.contenido = contenido;
        this.emisor = emisor;
        this.fecha = fecha;
        this.estado = 0;
    }

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     * @param fecha fecha del mensaje
     */
    private Mensaje(String contenido, Contacto emisor, Date fecha) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.fecha = fecha;
        this.estado = 0;
    }

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     */
    public Mensaje(String contenido) {
        this(contenido, Contacto.getSelf(), new Date());
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

    public void marcarEnviado() {
        DBOperations.obtenerInstancia(MainActivity.getMainActivity()).marcarEnviado(id);
    }

    public void registrar(Context contexto, Chat chat){
        DBOperations.obtenerInstancia(contexto).insertMessage(this,chat);
    }

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
