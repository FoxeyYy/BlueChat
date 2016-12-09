package asimov.uva.es.bluechat.sqllite;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Crea la base de datos a partir de las sentencias correspondientes para su creación
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class DBHelper extends SQLiteOpenHelper{

    /*Versión de la base de datos. Con cada nuevo cambio, el número se incrementa*/
    private static final int DATABASE_VERSION = 1;

    /*Nombre de la base de datos*/
    private static final String DATABASE_NAME = "Bluechat.db";

    /*Sentencia de creación de la tabla Contacto*/
    private static final String SQL_CREATE_TABLE_CONTACTO = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY" +
            ",%s TEXT, %s TEXT );", DBContract.Contacto.TABLE_NAME, DBContract.Contacto.COLUMN_NAME_MAC
            , DBContract.Contacto.COLUMN_NAME_NOMBRE, DBContract.Contacto.COLUMN_NAME_IMAGE);

    /* Sentencia de creación de la tabla Chat */
    private static final String SQL_CREATE_TABLE_CHAT = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY" + ", %s , %s TEXT);",
            DBContract.Chat.TABLE_NAME, DBContract.Chat.COLUMN_NAME_ID_CHAT, DBContract.Chat.COLUMN_NAME_ID_CONTACTO, DBContract.Chat.COLUMN_NAME_NOMBRE);

    /*Sentencia de creación de la tabla Mensaje*/
    private static final String SQL_CREATE_TABLE_MENSAJE = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY" +
            ",%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, FOREIGN KEY(%s) REFERENCES " +
            "Chat(%s));",
            DBContract.Mensaje.TABLE_NAME,
            DBContract.Mensaje.COLUMN_NAME_ID,
            DBContract.Mensaje.COLUMN_NAME_ID_CHAT,
            DBContract.Mensaje.COLUMN_NAME_CONTENT,
            DBContract.Mensaje.COLUMN_NAME_EMISOR,
            DBContract.Mensaje.COLUMN_NAME_FECHA,
            DBContract.Mensaje.COLUMN_NAME_STATUS,
            DBContract.Mensaje.COLUMN_NAME_ID_CHAT,
            DBContract.Chat.COLUMN_NAME_ID_CHAT);

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Crea la base de datos
     * @param db La base de datos que crea
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CONTACTO);
        db.execSQL(SQL_CREATE_TABLE_CHAT);
        db.execSQL(SQL_CREATE_TABLE_MENSAJE);
    }

    /**
     * Actualiza la base de datos a una versión más reciente o la sobreescribe
     * @param db base de datos a actualizar
     * @param oldVersion de los datos
     * @param newVersion de los datos
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Contacto.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + DBContract.Chat.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Mensaje.TABLE_NAME);
        db.setVersion(newVersion);
        onCreate(db);
    }
}
