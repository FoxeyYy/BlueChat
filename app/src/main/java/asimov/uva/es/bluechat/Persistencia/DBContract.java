package asimov.uva.es.bluechat.persistencia;

import android.provider.BaseColumns;

/**
 * Define la estructura de las tablas de la base de datos
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */

public class DBContract {

    private DBContract(){}

    /**
     * Define la tabla Contacto con sus respectivos atributos
     */
    public static class Contacto implements BaseColumns{
        public static final String TABLE_NAME = "Contacto";
        public static final String COLUMN_NAME_MAC = "mac";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_IMAGE = "image";
    }


    /**
     * Define la tabla Chat con sus respectivos atributos
     */
    public static class Chat implements BaseColumns{
        public static final String TABLE_NAME = "Chat";
        public static final String COLUMN_NAME_ID_CHAT = "idChat";
        public static final String COLUMN_NAME_ID_CONTACTO = "idContacto";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
    }

    /**
     * Define la tabla Chat Grupal con sus respectivos atributos
     */
    public static class ChatGrupal implements BaseColumns {
        public static final String TABLE_NAME = "ChatGrupal";
        public static final String COLUMN_NAME_ID_CHAT = "idChat";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
    }

    /**
     * Define la tabla de participantes de un chat grupal con sus respectivos atributos
     */
    public static class ParticipantesGrupo implements BaseColumns {
        public static final String TABLE_NAME = "ParticipantesGrupo";
        public static final String COLUMN_NAME_ID_CHAT = "idChat";
        public static final String COLUMN_NAME_ID_CONTACTO = "idContacto";
    }

    /**
     * Define la tabla de participantes de un chat grupal con sus respectivos atributos
     */
    public static class MensajePendiente implements BaseColumns {
        public static final String TABLE_NAME = "MensajePendiente";
        public static final String COLUMN_NAME_ID_MENSAJE = "idMensaje";
        public static final String COLUMN_NAME_ID_CONTACTO = "idContacto";
    }

    /**
     * Define la tabla Mensaje con sus respectivos atributos
     */
    public static class Mensaje implements BaseColumns{
        public static final String TABLE_NAME = "Mensaje";
        public static final String COLUMN_NAME_ID = "idMensaje";
        public static final String COLUMN_NAME_ID_CHAT = "idChat";
        public static final String COLUMN_NAME_CONTENT = "contenido";
        public static final String COLUMN_NAME_IMAGEN = "imagen";
        public static final String COLUMN_NAME_EMISOR = "emisor";
        public static final String COLUMN_NAME_FECHA = "fecha";
    }

}
