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
    private String direccionMac;
    private String nombre;

    /**
     * Direcion mac asociado al contacto
     */
    private String direccionMac;
    private String imagen;

    /**
     * Inicializa a los parámetros que se indican
     * @param direccionMac La MAC del usuario
     * @param nombre Nombre del usuario
     * @param imagen Imagen del usuario
     */
    public Contacto (String nombre, String direccionMac) {
    public Contacto (String direccionMac, String nombre, String imagen) {
        this.direccionMac = direccionMac;
        this.nombre = nombre;
        this.direccionMac = direccionMac;
        this.imagen = imagen;
    }

    /**
     * Obtiene la direccion mac del contacto
     * @return direccion mac del contacto
     */
    public String getDireccionMac() {
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccionMac(String direccionMac) {
        this.direccionMac = direccionMac;
    }
    public String getDireccionMac() {
        return direccionMac;
    }

    /**
     * Obtiene el nombre del contacto
     * @return nombre del contacto
     */
    public String getNombre() {
        return nombre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(direccionMac);
    }
    public String getImagen() {
        return imagen;
    }
}
