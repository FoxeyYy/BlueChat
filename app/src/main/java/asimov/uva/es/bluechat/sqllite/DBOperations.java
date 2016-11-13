package asimov.uva.es.bluechat.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by DAVID on 13/11/2016.
 */

public class DBOperations {

    private static DBHelper baseDatos;

    public static final String SQL_INSERT_MESSAGE = "";
    public static final String SQL_INSERT_CONTACT = "";
    public static final String SQL_READ_MESSAGE = "";
    public static final String SQL_READ_LAST_MESSAGES = "";
    public static final String SQL_READ_CONTACT = "";
    public static final String SQL_READ_ALL_CONTACTS = "";

    private static DBOperations instancia = new DBOperations();

    private DBOperations (){

    }

    public static DBOperations obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new DBHelper(contexto);
        }
        return instancia;
    }

    public void insertMessage(SQLiteDatabase db){

    }
    public void insertContact(SQLiteDatabase db){

    }
    public void readMessage(SQLiteDatabase db){

    }
    public void readContact(SQLiteDatabase db){

    }
    public void readLastMessages(SQLiteDatabase db){

    }
    public void readAllContacts(SQLiteDatabase db){

    }

    public SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}
