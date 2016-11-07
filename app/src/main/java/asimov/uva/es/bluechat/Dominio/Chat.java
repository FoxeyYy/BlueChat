package asimov.uva.es.bluechat.Dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hector on 6/11/16.
 * Clase representativa de un chat
 */
public class Chat {

    private Contacto par;

    private List<Mensaje> historial =  new ArrayList<>();


    public Chat(Contacto contacto) {
        par = contacto;
    }
    /**
     * Consigue todos los chats disponibles
     * @return la lista de chats
     */
    public static List<Chat> getChats() {
        List<Chat> chats = new ArrayList<>();

        chats.add(new Chat(new Contacto("Carlos")));
        chats.add(new Chat(new Contacto("Ana")));
        chats.add(new Chat(new Contacto("Diego")));

        return chats;
    }

    public Contacto getPar() {
        return par;
    }
}
