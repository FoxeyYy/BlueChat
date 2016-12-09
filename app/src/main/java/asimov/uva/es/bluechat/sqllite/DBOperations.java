package asimov.uva.es.bluechat.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    private static DBHelper baseDatos;

    /*Consultas a realizar por el gestor de bases de datos*/
    private static final String SQL_READ_MESSAGE = "SELECT * FROM Mensaje WHERE idMensaje = (SELECT MAX(idMensaje) FROM Mensaje);";
    private static final String SQL_READ_LAST_MESSAGES = "SELECT * FROM Mensaje ORDER BY idMensaje;";
    private static final String SQL_READ_CONTACT = "SELECT * FROM Contacto WHERE mac = ?;";
    private static final String SQL_READ_ALL_CONTACTS = "SELECT * FROM Contacto;";
    private static final String SQL_READ_CHAT = "SELECT * FROM Chat WHERE idChat = ?;";
    private static final String SQL_READ_ALL_CHATS = "SELECT * FROM Chat;";

    private static final DBOperations instancia = new DBOperations();

    private DBOperations (){}

    /**
     * Permite obtener instancias de la base de datos
     * @param contexto El contexto de acceso al servicio de base de datos
     */
    public static DBOperations obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new DBHelper(contexto);
        }
        return instancia;
    }

    /**
     * Inserta un mensaje en la base de datos
     * @param mensaje Mensaje que se va a insertar
     */
    public void insertMessage(Mensaje mensaje, Chat chat){
        ContentValues values = new ContentValues();
        values.put(DBContract.Mensaje.COLUMN_NAME_CONTENT, mensaje.getContenido());
        values.put(DBContract.Mensaje.COLUMN_NAME_EMISOR, mensaje.getEmisor().getDireccionMac());
        values.put(DBContract.Mensaje.COLUMN_NAME_FECHA, mensaje.getFecha().toString());
        values.put(DBContract.Mensaje.COLUMN_NAME_STATUS,0);
        values.put(DBContract.Mensaje.COLUMN_NAME_ID_CHAT,chat.getIdChat());

        /*Inserta la nueva fila*/
        getDb().insert(DBContract.Chat.TABLE_NAME, null, values);
    }

    /**
     * Inserta un contacto en la base de datos
     * @param contacto Contacto que se va a insertar
     */
    public void insertContact(Contacto contacto){
        ContentValues values = new ContentValues();
        values.put(DBContract.Contacto.COLUMN_NAME_MAC, contacto.getDireccionMac());
        values.put(DBContract.Contacto.COLUMN_NAME_NOMBRE, contacto.getNombre());
        values.put(DBContract.Contacto.COLUMN_NAME_IMAGE, contacto.getImagen());
        /*Inserta la nueva fila*/
        getDb().insert(DBContract.Contacto.TABLE_NAME, null, values);
    }

    /**
     * Inserta un contacto en la base de datos
     * @param chat El chat que se va a insertar
     */
    public void insertChat(Chat chat){
        ContentValues values = new ContentValues();
        values.put(DBContract.Chat.COLUMN_NAME_ID_CHAT, chat.getIdChat());
        values.put(DBContract.Chat.COLUMN_NAME_ID_CONTACTO, chat.getPar().getDireccionMac());
        values.put(DBContract.Chat.COLUMN_NAME_NOMBRE, chat.getNombre());
        /*Inserta una nueva fila*/
        getDb().insert(DBContract.Chat.TABLE_NAME, null, values);
    }

    /**
     * Devuelve el último mensaje enviado de un chat
     * @return cursor El cursor al último mensaje de un chat
     */
    public Cursor getLastMessage(){
        return getDb().rawQuery(SQL_READ_MESSAGE, null);
    }

    /**
     * Devuelve el contacto asociado a una MAC concreta
     * @param mac Mac del contacto
     * @return cursor El cursor al contacto
     */
    public Cursor getContact(String mac){
        String[] args = new String[] {mac};
        return getDb().rawQuery(SQL_READ_CONTACT, args);
    }

    /**
     * Devuelve los últimos mensajes de un chat
     * @return cursor El cursor a los últimos mensajes
     */
    public Cursor getLastMessages(){
        Cursor cursor = getDb().rawQuery(SQL_READ_LAST_MESSAGES, null);
        cursor.moveToLast();
        return cursor;
    }

    /**
     * Devuelve todos los contactos
     * @return cursor El cursor a los contactos
     */
    public Cursor getAllContacts(){
        Cursor cursor = getDb().rawQuery(SQL_READ_ALL_CONTACTS, null);
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * Devuelve un chat
     * @param idChat El identificador del chat
     * @return cursor El cursor al chat
     */
    public Cursor getChat(String idChat){
        String[] args = new String[] {idChat};
        Cursor cursor = getDb().rawQuery(SQL_READ_CHAT, args);
        return cursor;
    }

    /**
     * Devuelve todos los chats
     * @return cursor El cursor a los chats.
     */
    public Cursor getAllChats (){
        Cursor cursor = getDb().rawQuery(SQL_READ_ALL_CHATS, null);
        cursor.moveToLast();
        return cursor;
    }
    /**
     * Devuelve la base de datos en modo escritura
     * @return baseDatos La base de datos en modo escritura
     */
    private SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}
