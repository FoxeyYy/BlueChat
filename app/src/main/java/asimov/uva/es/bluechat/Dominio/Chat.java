package asimov.uva.es.bluechat.dominio;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import asimov.uva.es.bluechat.persistencia.DBContract;
import asimov.uva.es.bluechat.persistencia.DBOperations;

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
    private final List<Contacto> participantes;

    /**
     * Identifica al chat
     */
    private String idChat;

    /**
     * Nombre del chat
     */
    private final String nombre;

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

    /**
     * Devuelve los mensajes pendientes
     * @param context Contexto de la aplicación
     * @return mensajes La lista de mensajes pendientes
     */
    private List<Mensaje> getMensajesPendientes(Context context) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getMensajesPendientes(idChat);
        List<Mensaje> mensajes = new ArrayList();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_ID));
            String contenido = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_CONTENT));
            String imagen = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_IMAGEN));
            Contacto emisor = Contacto.getSelf(context);
            String fecha = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_FECHA));

            SimpleDateFormat formatoFecha = new SimpleDateFormat("hh:mm MMM dd yyyy", Locale.getDefault());
            Mensaje mensaje;
            try{
                Date date = formatoFecha.parse(fecha);
                mensaje = new Mensaje(id,contenido,imagen,emisor,date);
            }catch (ParseException e){
                e.printStackTrace();
                mensaje = new Mensaje(id,contenido,imagen,emisor,new Date());
            }
            mensajes.add(mensaje);
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
     * @param nombre El nombre del grupo
     * @param participantes Los contactos participantes en el grupo
     */
    public Chat(String nombre, List<Contacto> participantes) {
        this.nombre = nombre;
        this.participantes = new ArrayList(participantes);
        esPersistente = false;
        esGrupo = true;
    }

    /**
     * Inicializa un chat grupal
     * @param id El identificador del chat
     * @param nombre El nombre del grupo
     * @param participantes Los contactos participantes en el grupo
     */
    public Chat(String id, String nombre, List<Contacto> participantes) {
        this(nombre, participantes);
        esPersistente = true;
        esGrupo = true;
        idChat = id;
    }

    /**
     * Inicializa un chat a partir de un objeto parcelable
     * @param in El objeto parcelable
     */
    private Chat(Parcel in) {
        participantes = in.readArrayList(Contacto.class.getClassLoader());
        nombre = in.readString();
        idChat = in.readString();
        in.readList(historial, Mensaje.class.getClassLoader());
        esPersistente = in.readByte() != 0;
        esGrupo = in.readByte() != 0;
    }

    /**
     * El constructor del objeto parcelable
     */
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
     * Inicializa un chat
     * @param id El identificador del chat
     * @param nombre El nombre del chat
     * @param contacto El contacto del chat
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
     * @param context El contexto de la actividad
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

    /**
     * Devuelve los chats pendientes
     * @param context El contexto de la actividad
     * @return chats Lista de los chats pendientes
     */
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

    /**
     * Devuelve los mensajes
     * @param context El contexto de la actividad
     * @return mensajes La lista de mensajes
     */
    private List<Mensaje> getMensajes(Context context) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getMensajes(this);
        List<Mensaje> mensajes = new ArrayList();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_ID));
            String contenido = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_CONTENT));
            Contacto emisor = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_EMISOR)));
            String imagen = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_IMAGEN));
            String fecha = cursor.getString(cursor.getColumnIndex(DBContract.Mensaje.COLUMN_NAME_FECHA));

            SimpleDateFormat formatoFecha = new SimpleDateFormat("hh:mm MMM dd yyyy", Locale.getDefault());
            Mensaje mensaje;
            try{
                Date date = formatoFecha.parse(fecha);
                mensaje = new Mensaje(id,contenido,imagen,emisor,date);
                }catch (ParseException e){
                e.printStackTrace();
                mensaje = new Mensaje(id,contenido,imagen,emisor,new Date());
                }
            mensajes.add(mensaje);
        }

        cursor.close();

        return mensajes;
    }

    /**
     * Comprueba si el grupo se encuentra guardado en la base de datos
     * @param context El contexto de la actividad
     * @param id El identificador del grupo
     * @return true si el grupo existe, false en cualquier otro caso
     */
    public static boolean existeGrupo(Context context, String id){
        Cursor cursor = DBOperations.obtenerInstancia(context).getChatGrupal(id);
        return cursor.getCount() != 0;
    }

    /**
     * Devuelve un chat en grupo
     * @param context El contexto de la actividad
     * @param id El identificador del chat
     * @return chat El chat si existe
     */
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
     * @param context El contexto de la actividad
     */
    public void guardar(Context context) {

        if (!esGrupo()) {
            DBOperations.obtenerInstancia(context).insertChat(this);
        } else {
            DBOperations.obtenerInstancia(context).insertarGrupo(context, this);
            for (Contacto participante: participantes) {
                DBOperations.obtenerInstancia(context).insertContact(participante);
                DBOperations.obtenerInstancia(context).insertarContactoEnGrupo(this, participante);
            }
        }

        esPersistente = true;
    }

    /**
     * Devuelve un chat según su identificador
     * @param context El contexto de la actividad
     * @param idChat El identificador del chat
     * @return chat El chat si existe
     */
    public static Chat getChatById(Context context, String idChat) {
        Cursor cursor = DBOperations.obtenerInstancia(context).getChat(idChat);
        if(cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_NOMBRE));
            Contacto contacto = Contacto.getContacto(context, cursor.getString(cursor.getColumnIndex(DBContract.Chat.COLUMN_NAME_ID_CONTACTO)));

            Chat chat = new Chat(idChat, nombre, contacto);
            List<Mensaje> historial = chat.getMensajes(context);
            chat.setHistorial(historial);
            return chat;
        }
        return null;
    }

    /**
     * Comprueba si el chat esta almacenado de forma persistente
     * @return true si es persistente, false en otro caso
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean esPersistente () {
        return esPersistente;
    }

    /**
     * Comprueba si el chat es un grupo
     * @return true si es un grupo, false en otro caso
     */
    public boolean esGrupo() {
        return esGrupo;
    }

    /**
     * Establece el valor por defecto para el historial de mensajes
     * @param historial El historial del mensajes
     */
    private void setHistorial(List<Mensaje> historial) {
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
     * @return participantes La lista de participantes
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
     * @return nombre: El nombre del chat
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
