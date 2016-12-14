package asimov.uva.es.bluechat.Dominio;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import asimov.uva.es.bluechat.MainActivity;
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

    public static int ENVIADO = 1;
    public static int PENDIENTE = 0;

    private String id;
    private String contenido;
    private String imagen;
    private Contacto emisor;
    private Date fecha;
    private int estado;

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param imagen del mensaje
     */
    public Mensaje(String contenido, Uri imagen) {
        this(contenido, Contacto.getSelf(), new Date());
        this.imagen = imagen.toString();
    }

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     * @param fecha fecha del mensaje
     */
    public Mensaje(String id, String contenido, String imagen, Contacto emisor, Date fecha) {
        this(contenido, emisor, fecha);
        this.id = id;

        if (null != imagen && !imagen.isEmpty()) {
            this.imagen = imagen;
        }
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
        this.estado = PENDIENTE;
    }

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     */
    public Mensaje(String contenido) {
        this(contenido, Contacto.getSelf(), new Date());
    }


    protected Mensaje(Parcel in) {
        id = in.readString();
        contenido = in.readString();
        imagen = in.readString();
        emisor = in.readParcelable(Contacto.class.getClassLoader());
        fecha = new Date(in.readLong());
        estado = in.readInt();
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

    public String getImagen() {
        return imagen;
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

    /**
     * Modifica el valor de la imagen del mensaje
     * @param imagen path de la imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Obitene el id del mensaje
     * @return id del mensaje
     */
    public String getId() {
        return id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(contenido);
        dest.writeString(imagen);
        dest.writeParcelable(emisor, flags);
        dest.writeLong(fecha.getTime());
        dest.writeInt(estado);
    }
}
