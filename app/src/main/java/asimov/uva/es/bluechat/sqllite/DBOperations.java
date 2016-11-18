package asimov.uva.es.bluechat.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    private static final String SQL_READ_MESSAGE = "SELECT * FROM Chat WHERE idMensaje = (SELECT MAX(idMensaje) FROM Chat);";
    private static final String SQL_READ_LAST_MESSAGES = "SELECT * FROM Chat ORDER BY idMensaje;";
    private static final String SQL_READ_CONTACT = "SELECT * FROM Contacto WHERE mac = ?;";
    private static final String SQL_READ_ALL_CONTACTS = "SELECT * FROM Contacto;";


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
    public void insertMessage(Mensaje mensaje){
        ContentValues values = new ContentValues();
        values.put(DBContract.Chat.COLUMN_NAME_ID, mensaje.getIdMensaje());
        values.put(DBContract.Chat.COLUMN_NAME_MAC, mensaje.getMac());
        values.put(DBContract.Chat.COLUMN_NAME_CONTENT, mensaje.getContenido());
        values.put(DBContract.Chat.COLUMN_NAME_EMISOR, mensaje.getEmisor());
        values.put(DBContract.Chat.COLUMN_NAME_RECEPTOR, mensaje.getReceptor());
        values.put(DBContract.Chat.COLUMN_NAME_FECHA, mensaje.getFecha());

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
     * Devuelve la base de datos en modo escritura
     * @return baseDatos La base de datos en modo escritura
     */
    private SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}
