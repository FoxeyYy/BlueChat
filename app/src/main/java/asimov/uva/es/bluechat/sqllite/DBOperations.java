package asimov.uva.es.bluechat.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;

/**
 * Created by DAVID on 13/11/2016.
 */

public class DBOperations {

    private static DBHelper baseDatos;

    public static final String SQL_READ_MESSAGE = "SELECT * FROM Chat WHERE idMensaje = (SELECT MAX(idMensaje) FROM Chat);";
    public static final String SQL_READ_LAST_MESSAGES = "SELECT * FROM Chat ORDER BY idMensaje;";
    public static final String SQL_READ_CONTACT = "SELECT * FROM Contacto WHERE mac = ?;";
    public static final String SQL_READ_ALL_CONTACTS = "SELECT * FROM Contacto;";

    private static DBOperations instancia = new DBOperations();

    private DBOperations (){}

    public static DBOperations obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new DBHelper(contexto);
        }
        return instancia;
    }

    public void insertMessage(Mensaje mensaje){
        ContentValues values = new ContentValues();
        values.put(DBContract.Chat.COLUMN_NAME_ID, mensaje.getIdMensaje());
        values.put(DBContract.Chat.COLUMN_NAME_MAC, mensaje.getMac());
        values.put(DBContract.Chat.COLUMN_NAME_CONTENT, mensaje.getContenido());
        values.put(DBContract.Chat.COLUMN_NAME_EMISOR, mensaje.getEmisor());
        values.put(DBContract.Chat.COLUMN_NAME_RECEPTOR, mensaje.getReceptor());
        values.put(DBContract.Chat.COLUMN_NAME_FECHA, mensaje.getFecha());

        // Insert the new row
        getDb().insert(DBContract.Chat.TABLE_NAME, null, values);
    }
    public void insertContact(Contacto contacto){
        ContentValues values = new ContentValues();
        values.put(DBContract.Contacto.COLUMN_NAME_MAC, contacto.getDireccionMac());
        values.put(DBContract.Contacto.COLUMN_NAME_NOMBRE, contacto.getNombre());
        values.put(DBContract.Contacto.COLUMN_NAME_IMAGE, contacto.getImagen());

        getDb().insert(DBContract.Contacto.TABLE_NAME, null, values);
    }
    public Cursor getLastMessage(){
        Cursor cursor = getDb().rawQuery(SQL_READ_MESSAGE, null);
        return cursor;
    }
    public Cursor getContact(String mac){
        String[] args = new String[] {mac};
        Cursor cursor = getDb().rawQuery(SQL_READ_CONTACT, args);
        return cursor;
    }
    public Cursor readLastMessages(){
        Cursor cursor = getDb().rawQuery(SQL_READ_LAST_MESSAGES, null);
        cursor.moveToLast();
        return cursor;
    }
    public Cursor readAllContacts(){
        Cursor cursor = getDb().rawQuery(SQL_READ_ALL_CONTACTS, null);
        cursor.moveToFirst();
        return cursor;
    }

    private SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}
