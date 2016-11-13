package asimov.uva.es.bluechat.sqllite;

import android.provider.BaseColumns;

/**
 * Crea la estructura de las tablas para mejorar la mantenibilidad
 * Created by DAVID on 11/11/2016.
 */

public class DBContract {
    private DBContract(){}

    public static class Contacto implements BaseColumns{
        public static final String TABLE_NAME = "Contacto";
        public static final String COLUMN_NAME_MAC = "mac";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_IMAGE = "image";
    }

    public static class Chat implements BaseColumns{
        public static final String TABLE_NAME = "Chat";
        public static final String COLUMN_NAME_ID = "idMensaje";
        public static final String COLUMN_NAME_MAC = "mac";
        public static final String COLUMN_NAME_CONTENT = "contenido";
        public static final String COLUMN_NAME_EMISOR = "emisor";
        public static final String COLUMN_NAME_RECEPTOR = "receptor";
        public static final String COLUMN_NAME_FECHA = "fecha";

    }

}
