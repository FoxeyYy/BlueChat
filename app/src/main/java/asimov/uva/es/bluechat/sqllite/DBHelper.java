package asimov.uva.es.bluechat.sqllite;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Crea la base de datos a partir de las sentencias correspondientes para su creación
 * Created by DAVID on 11/11/2016.
 */
public class DBHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Bluechat.db";

    public static final String SQL_CREATE_TABLE_CONTACTO = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY" +
            ",%s TEXT, %s TEXT );", DBContract.Contacto.TABLE_NAME, DBContract.Contacto.COLUMN_NAME_MAC
            , DBContract.Contacto.COLUMN_NAME_NOMBRE, DBContract.Contacto.COLUMN_NAME_IMAGE);

    public static final String SQL_CREATE_TABLE_CHAT = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY" +
            ",%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, FOREIGN KEY(mac) REFERENCES " +
            "Chat(mac));", DBContract.Chat.COLUMN_NAME_ID,
            DBContract.Chat.COLUMN_NAME_MAC, DBContract.Chat.COLUMN_NAME_CONTENT,
            DBContract.Chat.COLUMN_NAME_EMISOR, DBContract.Chat.COLUMN_NAME_RECEPTOR, DBContract.Chat.COLUMN_NAME_FECHA);

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
    }

    /**
     * Actualiza la base de datos a una versión más reciente o la sobreescribe
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Contacto.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Chat.TABLE_NAME);
        onCreate(db);
    }
}
