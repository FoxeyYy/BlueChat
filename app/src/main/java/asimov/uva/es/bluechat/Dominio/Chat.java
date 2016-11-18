package asimov.uva.es.bluechat.Dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase representativa de un chat
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class Chat {

    /**
     * {@link Contacto} con el que el que se establece el chat
     */
    private Contacto par;

    /**
     * Historial de mensajes del chat
     */
    private List<Mensaje> historial =  new ArrayList<>();

    /**
     * Inicializa el chat
     * @param contacto con el cual se establece el chat
     */
    public Chat(Contacto contacto) {
        par = contacto;
    }

    /**
     * Consigue todos los chats disponibles
     * @return la lista de chats
     */
    public static List<Chat> getChats() {
        List<Chat> chats = new ArrayList<>();

        chats.add(new Chat(new Contacto("Carlos", "AA:BB:CC:DD:EE")));
        chats.add(new Chat(new Contacto("Ana","AA:BB:CC:DD:EE")));
        chats.add(new Chat(new Contacto("Diego","AA:BB:CC:DD:EE")));

        return chats;
    }

    /**
     * Obtiene el contacto con el que se ha establecido el chat
     * @return {@link Contacto} con el que se ha establecido el chat
     */
    public Contacto getPar() {
        return par;
    }
}
