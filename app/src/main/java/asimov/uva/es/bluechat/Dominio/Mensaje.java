package asimov.uva.es.bluechat.Dominio;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

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

    /**
     * Inicializa un Mensaje a los valores pasados por parámetro
     * @param contenido Contenido del mensaje
     * @param emisor Emisor del mensaje
     */
    public Mensaje(String contenido, Contacto emisor) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.fecha = new Date();
    }

    //TODO borrar
    public Mensaje(String msg) {
        this.contenido = msg;
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
