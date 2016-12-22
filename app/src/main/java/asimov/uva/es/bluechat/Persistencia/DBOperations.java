package asimov.uva.es.bluechat.Persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;

/**
 * Define las operaciones necesarias de la base de datos.
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */

public class DBOperations {

    /*Representación de la base de datos*/
    private static DBHelper baseDatos = null;

    /*Consultas a realizar por el gestor de bases de datos*/
    private static final String SQL_READ_MESSAGES = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY cast(%s as unsigned);", DBContract.Mensaje.TABLE_NAME, DBContract.Mensaje.COLUMN_NAME_ID_CHAT, DBContract.Mensaje.COLUMN_NAME_ID);
    private static final String SQL_READ_CONTACT = String.format("SELECT * FROM %s WHERE %s = ?;", DBContract.Contacto.TABLE_NAME, DBContract.Contacto.COLUMN_NAME_MAC);
    private static final String SQL_READ_ALL_CONTACTS = String.format("SELECT * FROM %s;", DBContract.Contacto.TABLE_NAME);
    private static final String SQL_READ_CHAT = String.format("SELECT * FROM %s WHERE %s = ?;", DBContract.Chat.TABLE_NAME, DBContract.Chat.COLUMN_NAME_ID_CHAT);
    private static final String SQL_READ_ALL_CHATS = String.format("SELECT * FROM %s", DBContract.Chat.TABLE_NAME);
    private static final String SQL_READ_ALL_GRUPOS = String.format("SELECT * FROM %s", DBContract.ChatGrupal.TABLE_NAME);
    private static final String SQL_READ_ALL_PARTICIPANTES_GRUPO = String.format("SELECT * FROM %s WHERE %s = ?", DBContract.ParticipantesGrupo.TABLE_NAME, DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CHAT);
    private static final String SQL_READ_PENDING_CHATS = String.format("SELECT * FROM %s WHERE %s IN (SELECT %s FROM %s m, %s mp WHERE m.%s = mp.%s) GROUP BY %s",
            DBContract.Chat.TABLE_NAME, DBContract.Chat.COLUMN_NAME_ID_CHAT, DBContract.Chat.COLUMN_NAME_ID_CHAT, DBContract.Mensaje.TABLE_NAME, DBContract.MensajePendiente.TABLE_NAME, DBContract.Mensaje.COLUMN_NAME_ID, DBContract.MensajePendiente.COLUMN_NAME_ID_MENSAJE, DBContract.Chat.COLUMN_NAME_ID_CHAT);
    private static final String SQL_READ_PENDING_GROUPS = String.format("SELECT * FROM %s WHERE %s IN (SELECT %s FROM %s m, %s mp WHERE m.%s = mp.%s) GROUP BY %s",
            DBContract.ChatGrupal.TABLE_NAME, DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT, DBContract.Mensaje.COLUMN_NAME_ID_CHAT, DBContract.Mensaje.TABLE_NAME, DBContract.MensajePendiente.TABLE_NAME, DBContract.Mensaje.COLUMN_NAME_ID, DBContract.MensajePendiente.COLUMN_NAME_ID_MENSAJE, DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT);
    private static final String SQL_READ_PENDING_MESSAGES_CHAT = String.format("SELECT * FROM %s JOIN %s USING(%s) WHERE %s = ? GROUP BY %s ",
            DBContract.MensajePendiente.TABLE_NAME, DBContract.Mensaje.TABLE_NAME,DBContract.MensajePendiente.COLUMN_NAME_ID_MENSAJE, DBContract.Mensaje.COLUMN_NAME_ID_CHAT, DBContract.Mensaje.COLUMN_NAME_ID);
    private static final String SQL_GET_NUM_CHATS = String.format("SELECT COUNT(*) FROM %s", DBContract.Chat.TABLE_NAME);
    private static final String SQL_GET_NUM_GRUPOS = String.format("SELECT COUNT(*) FROM %s", DBContract.ChatGrupal.TABLE_NAME);
    private static final String SQL_GET_NUM_MSG = String.format("SELECT COUNT(*) FROM %s", DBContract.Mensaje.TABLE_NAME);
    private static final String SQL_READ_GROUP_CHAT = String.format("SELECT * FROM %s JOIN %s USING(%s) WHERE %s =? GROUP BY %S",
            DBContract.ChatGrupal.TABLE_NAME,
            DBContract.ParticipantesGrupo.TABLE_NAME,
            DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT,
            DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT,
            DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT);
    private static final String SQL_READ_PARTICIPANTES_CON_MENSAJES_PENDIENTES = String.format("SELECT * FROM %s pg, %s mp WHERE %s = ? AND pg.%s = mp.%s GROUP BY pg.%s",
            DBContract.ParticipantesGrupo.TABLE_NAME, DBContract.MensajePendiente.TABLE_NAME, DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CHAT, DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CONTACTO, DBContract.MensajePendiente.COLUMN_NAME_ID_CONTACTO, DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CONTACTO);


    private static final DBOperations instancia = new DBOperations();

    private DBOperations (){}

    /**
     * Permite obtener instancias de la base de datos
     * @param contexto El contexto de acceso al servicio de base de datos
     */
    public static DBOperations obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            try {
                baseDatos = new DBHelper(contexto);
            } catch (ExceptionInInitializerError e) {
                Log.e("excepcion", e.getCause().toString());
            }
        }
        return instancia;
    }

    /**
     * Inserta un mensaje en la base de datos
     * @param mensaje Mensaje que se va a insertar
     * @param chat al que pertenece
     * @param pendiente true si el mensaje es enviado por el usuario, false en cualquier otro caso
     */
    public void insertMessage(Mensaje mensaje, Chat chat, boolean pendiente){
        int num = getNumMensajes();
        String imagen;
        if (null == mensaje.getImagen()) {
            imagen = "";
        } else {
            imagen = mensaje.getImagen();
        }

        ContentValues values = new ContentValues();
        values.put(DBContract.Mensaje.COLUMN_NAME_ID, num +1);
        values.put(DBContract.Mensaje.COLUMN_NAME_CONTENT, mensaje.getContenido());
        values.put(DBContract.Mensaje.COLUMN_NAME_IMAGEN, imagen);
        values.put(DBContract.Mensaje.COLUMN_NAME_EMISOR, mensaje.getEmisor().getDireccionMac());
        values.put(DBContract.Mensaje.COLUMN_NAME_FECHA, mensaje.getFecha().toString());
        values.put(DBContract.Mensaje.COLUMN_NAME_ID_CHAT, chat.getIdChat());

        if(pendiente) {
            for (Contacto contacto : chat.getParticipantes()) {
                ContentValues values1 = new ContentValues();
                values1.put(DBContract.MensajePendiente.COLUMN_NAME_ID_MENSAJE, num + 1);
                values1.put(DBContract.MensajePendiente.COLUMN_NAME_ID_CONTACTO, contacto.getDireccionMac());
                getDb().insert(DBContract.MensajePendiente.TABLE_NAME, null, values1);
            }
        }

        /*Inserta la nueva fila*/
        getDb().insert(DBContract.Mensaje.TABLE_NAME, null, values);
    }

    /**
     * Inserta un contacto en la base de datos
     * @param contacto El contacto que se va a insertar
     */
    public void insertContact(Contacto contacto){
        Cursor cursor = getContact(contacto.getDireccionMac());
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            cursor.close();
            updateContacto(contacto);
        }else {

            ContentValues values = new ContentValues();
            values.put(DBContract.Contacto.COLUMN_NAME_MAC, contacto.getDireccionMac());
            values.put(DBContract.Contacto.COLUMN_NAME_NOMBRE, contacto.getNombre());
            values.put(DBContract.Contacto.COLUMN_NAME_IMAGE, contacto.getImagen());
        /*Inserta la nueva fila*/
            getDb().insert(DBContract.Contacto.TABLE_NAME, null, values);
        }
    }

    /**
     * Inserta un chat en la base de datos
     * @param chat El chat que se va a insertar
     */
    public void insertChat(Chat chat){
        ContentValues values = new ContentValues();
        if(!chat.esGrupo()) {
            int num = getNumChats();
            chat.setIdChat(String.valueOf(num + 1));
            values.put(DBContract.Chat.COLUMN_NAME_ID_CHAT, num + 1);
        }else
            values.put(DBContract.Chat.COLUMN_NAME_ID_CHAT, chat.getIdChat());
        values.put(DBContract.Chat.COLUMN_NAME_ID_CONTACTO, chat.getPar().getDireccionMac());
        values.put(DBContract.Chat.COLUMN_NAME_NOMBRE, chat.getNombre());
        /*Inserta una nueva fila*/
        getDb().insert(DBContract.Chat.TABLE_NAME, null, values);
    }

    /**
     * Inserta un nuevo chat grupal en la base de datos
     * @param chat El chat a insertar
     */
    public void insertarGrupo (Chat chat) {
        int id;
        if(!chat.esPersistente()){
            id = getNumGrupos() + Contacto.getSelf().getDireccionMac().hashCode();
            chat.setIdChat(String.valueOf(id));
        }else
            id = Integer.valueOf(chat.getIdChat());
        ContentValues values = new ContentValues();
        values.put(DBContract.ChatGrupal.COLUMN_NAME_ID_CHAT, id);
        values.put(DBContract.ChatGrupal.COLUMN_NAME_NOMBRE, chat.getNombre());

        getDb().insert(DBContract.ChatGrupal.TABLE_NAME, null, values);
    }

    /**
     * Asocia un grupo a un contacto
     * @param chat El chat a asociar
     * @param contacto El contacto a asociar
     */
    public void insertarContactoEnGrupo (Chat chat, Contacto contacto) {
        ContentValues values = new ContentValues();
        values.put(DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CHAT, chat.getIdChat());
        values.put(DBContract.ParticipantesGrupo.COLUMN_NAME_ID_CONTACTO, contacto.getDireccionMac());

        getDb().insert(DBContract.ParticipantesGrupo.TABLE_NAME, null, values);
    }

    /**
     * Devuelve el número de grupos existentes en la base de datos
     * @return num El número de grupos
     */
    private int getNumGrupos() {
        int num = 0;

        Cursor cursor = getDb().rawQuery(SQL_GET_NUM_GRUPOS,null);
        if(null != cursor && cursor.moveToFirst()){
            num = cursor.getInt(0);
        }

        return num;
    }

    /**
     * Devuelve el número de chats existentes en la base de datos
     * @return num El número de chats
     */
    private int getNumChats() {
        int num = 0;

        Cursor cursor = getDb().rawQuery(SQL_GET_NUM_CHATS,null);
        if(null != cursor && cursor.moveToFirst()){
            num = cursor.getInt(0);
        }

        return num;
    }

    /**
     * Devuelve el número de mensajes existentes en la base de datos
     * @return num El número de mensajes
     */
    private int getNumMensajes() {
        int num = 0;

        Cursor cursor = getDb().rawQuery(SQL_GET_NUM_MSG,null);
        if(null != cursor && cursor.moveToFirst()){
            num = cursor.getInt(0);
        }

        return num;
    }

    /**
     * Devuelve el contacto asociado a una MAC concreta
     * @param mac La MAC del contacto
     * @return cursor El cursor que apunta al contacto
     */
    public Cursor getContact(String mac){
        String[] args = new String[] {mac};
        return getDb().rawQuery(SQL_READ_CONTACT, args);
    }

    /**
     * Devuelve los mensajes de un chat
     * @return cursor El cursor a los últimos mensajes
     */
    public Cursor getMensajes(Chat chat){
        String[] args = new String[] {chat.getIdChat()};
        return getDb().rawQuery(SQL_READ_MESSAGES, args);
    }

    /**
     * Devuelve todos los contactos
     * @return cursor El cursor a los contactos
     */
    public Cursor getAllContacts(){
        return getDb().rawQuery(SQL_READ_ALL_CONTACTS, null);
    }

    /**
     * Devuelve un chat
     * @param idChat El identificador del chat
     * @return cursor El cursor al chat
     */
    public Cursor getChat(String idChat){
        String[] args = new String[] {idChat};
        return getDb().rawQuery(SQL_READ_CHAT, args);
    }


    /**
     * Devuelve todos los chats
     * @return cursor El cursor a los chats.
     */
    public Cursor getAllChats (){
        return getDb().rawQuery(SQL_READ_ALL_CHATS, null);
    }

    /**
     * Devuelve todos los chats grupales
     * @return cursor El cursor a los chats grupales.
     */
    public Cursor getGrupos () {
        return getDb().rawQuery(SQL_READ_ALL_GRUPOS, null);
    }

    /**
     * Devuelve un cursor a todos los participantes de un grupo
     * @return cursor El cursor a los participantes
     */
    public Cursor getParticipantesGrupo (String id) {
        String[] args = {id};
        return getDb().rawQuery(SQL_READ_ALL_PARTICIPANTES_GRUPO, args);
    }

    /**
     * Devuelve un cursor a todos los participantes de un grupo con mensajes pendientes
     * @return cursor El cursor a los participantes
     */
    public Cursor getParticipantesConMensajesPendientes(String id){
        String[] args = {id};
        return getDb().rawQuery(SQL_READ_PARTICIPANTES_CON_MENSAJES_PENDIENTES, args);
    }

    /**
     * Devuelve un cursor a todos los chats pendientes
     * @return cursor El cursor a los chats
     */
    public Cursor getChatsPendientes(){
        Cursor cursor = getDb().rawQuery(SQL_READ_PENDING_CHATS, null);
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * Devuelve un cursor a todos los grupos pendientes
     * @return cursor El cursor a los grupos
     */
    public Cursor getGruposPendientes(){
        Cursor cursor = getDb().rawQuery(SQL_READ_PENDING_GROUPS, null);
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * Devuelve un cursor a todos los chats pendientes
     * @return cursor El cursor a los chats
     */
    public Cursor getMensajesPendientes(String idChat){
        String[] args = new String[] {idChat};
        return getDb().rawQuery(SQL_READ_PENDING_MESSAGES_CHAT, args);
    }

    /**
     * Devuelve un cursor a un chat grupal
     * @param idChat El identificador de chat
     * @return cursor El cursor al chat grupal
     */
    public  Cursor getChatGrupal(String idChat){
        String[] args = new String[] {idChat};
        return getDb().rawQuery(SQL_READ_GROUP_CHAT, args);
    }

    /**
     * Devuelve la base de datos en modo escritura
     * @return baseDatos La base de datos en modo escritura
     */
    private SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }

    /**
     * Marca un mensaje como enviado
     * @param idMensaje El identificador del del mensaje
     * @param idContacto El identificador del contacto
     */
    public void marcarEnviado(String idMensaje, String idContacto) {
        String[] args = new String[] {idMensaje, idContacto};
        getDb().delete(DBContract.MensajePendiente.TABLE_NAME,"idMensaje = ? AND idContacto = ?", args);
    }

    /**
     * Actualiza un contacto con la nueva informacion
     * @param contacto El contacto a actualizar
     */
    private void updateContacto(Contacto contacto){
        String nombre = contacto.getNombre();
        String mac = contacto.getDireccionMac();
        String imagen = contacto.getImagen();
        String[] args = new String[]{mac};
        ContentValues values = new ContentValues();
        values.put(DBContract.Contacto.COLUMN_NAME_NOMBRE, nombre);
        values.put(DBContract.Contacto.COLUMN_NAME_IMAGE, imagen);
        getDb().update(DBContract.Contacto.TABLE_NAME,values, "mac = ?", args);

    }
}
