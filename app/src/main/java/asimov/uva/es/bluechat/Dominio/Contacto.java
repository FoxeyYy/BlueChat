package asimov.uva.es.bluechat.Dominio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contacto de la App,
 * contiene datos identificativos para una persona
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class Contacto implements Parcelable{

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
