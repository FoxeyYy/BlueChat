package asimov.uva.es.bluechat.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by DAVID on 13/11/2016.
 */

public class DBOperations {

    private static DBHelper baseDatos;

    public static final String SQL_READ_MESSAGE = "SELECT * FROM Chat WHERE idMensaje = (SELECT MAX(idMensaje) FROM Chat);";
    public static final String SQL_READ_LAST_MESSAGES = "SELECT * FROM Chat ORDER BY idMensaje DESC LIMIT 10;";
    public static final String SQL_READ_CONTACT = "SELECT * FROM Contacto WHERE mac = ?";
    public static final String SQL_READ_ALL_CONTACTS = "SELECT * FROM Contacto ORDER BY mac";

    private static DBOperations instancia = new DBOperations();

    private DBOperations (){}

    public static DBOperations obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new DBHelper(contexto);
        }
        return instancia;
    }

    public void insertMessage(int id, String mac, String contenido, String emisor, String receptor,
                              String fecha){
        ContentValues values = new ContentValues();
        values.put(DBContract.Chat.COLUMN_NAME_ID, id);
        values.put(DBContract.Chat.COLUMN_NAME_MAC, mac);
        values.put(DBContract.Chat.COLUMN_NAME_CONTENT, contenido);
        values.put(DBContract.Chat.COLUMN_NAME_EMISOR, emisor);
        values.put(DBContract.Chat.COLUMN_NAME_RECEPTOR, receptor);
        values.put(DBContract.Chat.COLUMN_NAME_FECHA, fecha);

        // Insert the new row
        getDb().insert(DBContract.Chat.TABLE_NAME, null, values);
    }
    public void insertContact(int mac, int nombre, String imagen){
        ContentValues values = new ContentValues();
        values.put(DBContract.Contacto.COLUMN_NAME_MAC, mac);
        values.put(DBContract.Contacto.COLUMN_NAME_NOMBRE, nombre);
        values.put(DBContract.Contacto.COLUMN_NAME_IMAGE, imagen);

        getDb().insert(DBContract.Contacto.TABLE_NAME, null, values);
    }
    public Cursor readMessage(){
        Cursor cursor = getDb().rawQuery(SQL_READ_MESSAGE, null);
        return cursor;
    }
    public Cursor readContact(String mac){
        String[] args = new String[] {mac};
        Cursor cursor = getDb().rawQuery(SQL_READ_CONTACT, args);
        return cursor;
    }
    public Cursor readLastMessages(){
        Cursor cursor = getDb().rawQuery(SQL_READ_LAST_MESSAGES, null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor readAllContacts(){
        Cursor cursor = getDb().rawQuery(SQL_READ_ALL_CONTACTS, null);
        cursor.moveToFirst();
        return cursor;
    }

    public SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}
