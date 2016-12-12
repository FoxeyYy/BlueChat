package asimov.uva.es.bluechat.Dominio;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import asimov.uva.es.bluechat.sqllite.DBContract;
import asimov.uva.es.bluechat.sqllite.DBOperations;

/**
 * Clase representativa de un chat
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class Chat implements Parcelable{

    /**
     * {@link Contacto} con el que el que se establece el chat
     */
    private Contacto par;

    /**
     * Identifica al chat
     */
    private String idChat;

    /**
     * Nombre del chat
     */
    private String nombre;

    /**
     * Historial de mensajes del chat
     */
    private List<Mensaje> historial =  new ArrayList<>();

    /**
     * Historial de chats
     */
    private static List<Chat> chats = new ArrayList<>();

    /**
     * Inicializa el chat
     * @param contacto El contacto con el cual se establece el chat
     */
    public Chat(Contacto contacto) {
        par = contacto;
    }

    protected Chat(Parcel in) {
        par = in.readParcelable(Contacto.class.getClassLoader());
        in.readList(historial, Mensaje.class.getClassLoader());
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    /**
     * Construye un chat
     * @param id del chat
     * @param nombre del chat
     * @param historial de mensajes del chat
     * @param contacto del chat
     */
    public Chat(String id, String nombre, List<Mensaje> historial, Contacto contacto) {
        this.idChat = id;
        this.nombre = nombre;
        this.historial = historial;
        this.par = contacto;
    }

    /**
     * Carga todos los chats disponibles
     */
    public static void cargarChats(Context context) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getAllChats();

        chats.clear();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CHAT));
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_NOMBRE));
            Contacto contacto = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CONTACTO)));
            List<Mensaje> historial = Mensaje.getMensajes(context, idChat);

            chats.add(new Chat(idChat, nombre, historial, contacto));
        }

        cursor.close();
    }

    public static List<Chat> getChatsPendientes(Context context){
        Cursor cursor = DBOperations.obtenerInstancia(context).getChatsPendientes();

        List<Chat> chats = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CHAT));

            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_NOMBRE));
            Contacto contacto = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CONTACTO)));
            List<Mensaje> historial = Mensaje.getMensajesPendientes(context, idChat);

            chats.add(new Chat(idChat, nombre, historial, contacto));
        }

        cursor.close();

        return chats;
    }

    /**
     * Consigue todos los chats disponibles
     * @return chats La lista de chats
     */
    public static List<Chat> getChats() {
        return chats;
    }

    /**
     * Devuelve este chat
     * @param contacto el contacto del que buscar el chat
     * @return
     */
    public static Chat getChat(Contacto contacto) {
        Chat chat = null;
        for(int i = 0; i< getChats().size();i++) {
            chat = getChats().get(i);
            if(chat.getPar().equals(contacto)){
                break;
            }
        }
        return chat;
    }

    /**
     * Guarda el chat
     * @param context de la actividad
     */
    public void guardar(Context context) {
        DBOperations.obtenerInstancia(context).insertChat(this);
    }

    /**
     * Establece el valor para el contacto con el que se establece el chat
     */
    public void setPar(Contacto par) {
        this.par = par;
    }

    /**
     * Establece el valor por defecto para el historial de mensajes
     */
    public void setHistorial(List<Mensaje> historial) {
        this.historial = historial;
    }

    /**
     * Devuelve el historial de mensajes para el chat
     * @return historial El historial de mensajes
     */
    public List<Mensaje> getHistorial() {
        return historial;
    }

    /**
     * Obtiene el contacto con el que se ha establecido el chat
     * @return {@link Contacto} con el que se ha establecido el chat
     */
    public Contacto getPar() {
        return par;
    }

    /**
     * Devuelve el identificador del chat
     * @return idChat El identificador del chat
     */
    public String getIdChat() {
        return idChat;
    }

    /**
     * Devuelve el nombre del chat
     * @return el nombre
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el valor para el identificador del chat
     * @param idChat El valor proporcionado
     */
    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(par, 0);
        dest.writeList(historial);
    }
}
