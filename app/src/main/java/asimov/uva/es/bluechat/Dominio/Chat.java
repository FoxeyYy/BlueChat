package asimov.uva.es.bluechat.Dominio;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
     * {@link Contacto} con los que se establece el chat
     */
    private List<Contacto> participantes;

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
     * Indicador de persistencia en BBDD
     */
    private boolean esPersistente;

    /**
     * Indicador de chat grupal
     */
    private boolean esGrupo;

    private List<Mensaje> getMensajesPendientes(Context context) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getMensajesPendientes(idChat);
        List<Mensaje> mensajes = new ArrayList();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_ID));
            String contenido = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_CONTENT));
            String imagen = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_IMAGEN));
            Contacto emisor = Contacto.getSelf(); //TODO nosotros mismos en la base? o siempre self
            String fecha = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_FECHA));

            mensajes.add(new Mensaje(id, contenido, imagen, emisor, new Date())); //TODO Fecha de la bbdd
        }

        cursor.close();

        return mensajes;
    }

    /**
     * Inicializa el chat
     * @param contacto El contacto con el cual se establece el chat
     */
    public Chat(Contacto contacto) {
        participantes = new ArrayList(1);
        participantes.add(contacto);
        nombre = contacto.getNombre();
        esPersistente = false;
        esGrupo = false;
    }

    /**
     * Inicializa un chat grupal
     * @param nombre del grupo
     * @param participantes del grupo
     */
    public Chat(String nombre, List<Contacto> participantes) {
        this.nombre = nombre;
        this.participantes = new ArrayList(participantes);
        esPersistente = false;
        esGrupo = true;
    }

    /**
     * Inicializa un chat grupal
     * @param id del chat
     * @param nombre del grupo
     * @param participantes del grupo
     */
    public Chat(String id, String nombre, List<Contacto> participantes) {
        this(nombre, participantes);
        esPersistente = true;
        esGrupo = true;
        idChat = id;
    }

    protected Chat(Parcel in) {
        participantes = in.readArrayList(Contacto.class.getClassLoader());
        nombre = in.readString();
        idChat = in.readString();
        in.readList(historial, Mensaje.class.getClassLoader());
        esPersistente = in.readByte() != 0;
        esGrupo = in.readByte() != 0;
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
     * @param contacto del chat
     */
    private Chat(String id, String nombre, Contacto contacto) {
        this.idChat = id;
        this.nombre = nombre;
        participantes = new ArrayList(1);
        participantes.add(contacto);
        esPersistente = true;
        esGrupo = false;
    }

    /**
     * Devuelve todos los chats disponibles
     */
    public static List<Chat> getChats(Context context) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getAllChats();
        List<Chat> chats = new ArrayList();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CHAT));
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_NOMBRE));
            Contacto contacto = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CONTACTO)));

            Chat chat = new Chat(idChat, nombre, contacto);
            List<Mensaje> historial = chat.getMensajes(context);
            chat.setHistorial(historial);

            chats.add(chat);
        }

        cursor.close();

        cursor = DBOperations.obtenerInstancia(context).getGrupos();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT));
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.ChatGrupal.COLUMN_NAME_NOMBRE));

            List<Contacto> participantes = Contacto.getParticipantesGrupo(context, idChat);
            Chat chat = new Chat(idChat, nombre, participantes);
            List<Mensaje> historial = chat.getMensajes(context);
            chat.setHistorial(historial);

            chats.add(chat);
        }

        cursor.close();

        return chats;
    }

    public static List<Chat> getChatsPendientes(Context context){
        Cursor cursor = DBOperations.obtenerInstancia(context).getChatsPendientes();
        List<Chat> chats = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CHAT));
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_NOMBRE));
            Contacto contacto = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CONTACTO)));
            Chat chat = new Chat(idChat, nombre, contacto);

            List<Mensaje> historial = chat.getMensajesPendientes(context);
            chat.setHistorial(historial);

            chats.add(chat);
        }
        cursor.close();

        cursor = DBOperations.obtenerInstancia(context).getGruposPendientes();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT));
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.ChatGrupal.COLUMN_NAME_NOMBRE));

            List<Contacto> participantes = Contacto.getParticipantesConMensajesPendientes(context, idChat);
            Chat chat = new Chat(idChat, nombre, participantes);
            List<Mensaje> historial = chat.getMensajesPendientes(context);
            chat.setHistorial(historial);

            chats.add(chat);
        }

        cursor.close();

        return chats;
    }

    public List<Mensaje> getMensajes(Context context) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getMensajes(this);
        List<Mensaje> mensajes = new ArrayList();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_ID));
            String contenido = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_CONTENT));
            Contacto emisor = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_EMISOR)));
            String imagen = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_IMAGEN));
            String fecha = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_FECHA));
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = formatoFecha.parse(fecha);
                Mensaje mensaje = new Mensaje(id, contenido, imagen, emisor, date);
                mensajes.add(mensaje); //TODO Fecha de la bbdd
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        cursor.close();

        return mensajes;
    }

    /**
     * Comprueba si el grupo se encuentra guardado en la db
     * @param context
     * @param id del grupo
     * @return true si el grupo existe, false en cualquier otro caso
     */
    public static boolean existeGrupo(Context context, String id){
        Cursor cursor = DBOperations.obtenerInstancia(context).getChatGrupal(id);
        return cursor.getCount() != 0;
    }

    public static Chat getChatGrupal(Context context, String id){
        List<Chat> chats = new ArrayList<>();
        Cursor cursor = DBOperations.obtenerInstancia(context).getGrupos();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idChat = cursor.getString(cursor.getColumnIndex(DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT));
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.ChatGrupal.COLUMN_NAME_NOMBRE));

            List<Contacto> participantes = Contacto.getParticipantesGrupo(context, idChat);
            Chat chat = new Chat(idChat, nombre, participantes);
            List<Mensaje> historial = chat.getMensajes(context);
            chat.setHistorial(historial);

            chats.add(chat);
        }
        cursor.close();

        for(Chat chat: chats){
            if(chat.getIdChat().equals(id))
                return chat;
        }

        return null;
    }

    /**
     * Guarda el chat
     * @param context de la actividad
     */
    public void guardar(Context context) {

        if (!esGrupo()) {
            DBOperations.obtenerInstancia(context).insertChat(this);
        } else {
            DBOperations.obtenerInstancia(context).insertarGrupo(this);
            for (Contacto participante: participantes) {
                DBOperations.obtenerInstancia(context).insertContact(participante);
                DBOperations.obtenerInstancia(context).insertarContactoEnGrupo(this, participante);
            }
        }

        esPersistente = true;
    }

    public boolean esPersistente () {
        return esPersistente;
    }

    public boolean esGrupo() {
        return esGrupo;
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
     * Obtiene el contacto con el que se ha establecido el chat en caso de no ser un grupo
     * @return {@link Contacto} con el que se ha establecido el chat, null en caso de ser un grupo
     */
    public Contacto getPar() {
        return esGrupo() ? null : participantes.get(0);
    }

    /**
     * Devuelve la lista de participantes del chat
     * @return la lista de participantes
     */
    public List<Contacto> getParticipantes () {
        return participantes;
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

    public void setParticipantes(List<Contacto> participantes){this.participantes = participantes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(participantes);
        dest.writeString(nombre);
        dest.writeString(idChat);
        dest.writeList(historial);
        dest.writeByte((byte) (esPersistente ? 1 : 0));
        dest.writeByte((byte) (esGrupo ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chat)) return false;

        Chat chat = (Chat) o;

        return idChat != null ? idChat.equals(chat.idChat) : chat.idChat == null;

    }
}
