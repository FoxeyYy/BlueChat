package asimov.uva.es.bluechat.serviciosConexion;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import asimov.uva.es.bluechat.R;
import asimov.uva.es.bluechat.controladoresVistas.ActivityPrincipal;
import asimov.uva.es.bluechat.dominio.Chat;
import asimov.uva.es.bluechat.dominio.Contacto;
import asimov.uva.es.bluechat.dominio.Mensaje;
import asimov.uva.es.bluechat.dominio.paquetesBluetooth.Peticion;
import asimov.uva.es.bluechat.dominio.paquetesBluetooth.RespuestaPeticion;

/**
 * Hilo encargado de la transmisión de los mensajes una vez se ha establecido la conexión
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ConexionBluetooth extends Thread {

    /**
     * Enumeración para indicar el modo de la conexión
      */
    public enum Modo {
        SERVIDOR,
        CLIENTE_MENSAJES,
        CLIENTE_MENSAJES_GRUPO
    }

    /**
     * Stream de entrada del {@link BluetoothSocket}
     */
    private ObjectInputStream entrada = null;

    /**
     * Stream de salida del {@link BluetoothSocket}
     */
    private ObjectOutputStream salida = null;

    /**
     * El chat de la conexion
     */
    private Chat chatConexion;

    private final String ERROR = "ERROR";
    private final String CONEXION = "CONEXION";

    /**
     * El socket para el Bluetooth
     */
    private final BluetoothSocket socket;

    /**
     * Lista de mensajes
     */
    private List<Mensaje> mensajes = null;

    private final String IMAGEN = "Imagen";

    private final Context contexto;

    //TODO enviar idgrupo de forma correcta
    private String idGrupo;
    public void setIdGrupo(String idGrupo){this.idGrupo = idGrupo;}

    /**
     * Modo de ejecución
     */
    private final Modo modo;

    /**
     * Inicaliza los streams de la conexión bluetooth a partir del socket de la misma
     * @param socket El socket de la conexión
     * @param modo   El modo de ejecucion
     */
    public ConexionBluetooth(Context context, BluetoothSocket socket, Modo modo) {
        Log.d(CONEXION, "CONEXION BUENA");
        this.contexto = context;
        this.socket = socket;
        this.modo = modo;

        try {
            /* Primero, crear siempre el output stream, sino se producirá un bloqueo indefinido.
            A serialization stream header is read from the stream and verified.
            This constructor will block until the corresponding ObjectOutputStream has written and flushed the header. */
            OutputStream tmpOut = this.socket.getOutputStream();
            salida = new ObjectOutputStream(tmpOut);

            InputStream tmpIn = this.socket.getInputStream();
            entrada = new ObjectInputStream(tmpIn);

        } catch (IOException e) {
            Log.e(ERROR, "Thread de conexion no puede obtener los streams");
            e.printStackTrace();
        }

    }

    /**
     * Inicaliza los streams de la conexión bluetooth a partir del socket de la misma
     * @param socket  El socket de la conexión
     * @param modo    El modo de ejecucion
     * @param mensaje El mensaje a enviar
     */
    public ConexionBluetooth(Context context,BluetoothSocket socket, Modo modo, List<Mensaje> mensaje) {
        this(context, socket, modo);
        this.mensajes = mensaje;
    }


    @Override
    public void run() {

        Log.d(CONEXION, "Ejecutando...");

        switch (modo) {
            case CLIENTE_MENSAJES:
                enviarMensajeChat();
                break;
            case CLIENTE_MENSAJES_GRUPO:
                enviarMensajesGrupo();
                break;
            case SERVIDOR:
                servidor();
                break;
            default:
                Log.e(ERROR, "Modo incorrecto");
                break;
        }

        try {
            salida.close();
            entrada.close();
        } catch (IOException e) {
            Log.e(ERROR, "No se pueden cerrar los streams");
        }

    }

    /**
     * Envia mensaje de un chat
     */
    private void enviarMensajeChat() {
        Log.d(CONEXION, "Enviando mensajes...");
        solicitarEnvioMensajes();
        if (solicitudAceptada()) {
            enviarDescubrimiento();
            enviarMensajes();
        } else {
            Log.e(ERROR, "Solicitud rechadaza");
        }

    }

    /**
     * Envia mensaje de un grupo
     */
    private void enviarMensajesGrupo(){
        solicitarEnvioMensajesGrupo(idGrupo);
        if(!solicitudAceptada())
            enviarInfoGrupo(idGrupo);
        enviarDescubrimiento();
        enviarMensajes();

    }

    /**
     * Envia mensajes
     */
    private void enviarMensajes(){
        for(Mensaje mensaje : mensajes) {
            enviar(mensaje);
            if (null != mensaje.getImagen()) {
                byte[] imagen = getBytesImagen(mensaje.getImagen());
                enviar(imagen);
            }
            mensaje.marcarEnviado(contexto, socket.getRemoteDevice().getAddress());
        }
    }

    /**
     * Recibe peticiones y actúa en consecuencia
     */
    private void servidor() {

        Log.d(CONEXION, "Escuchando...");

        try {
            Peticion peticion = (Peticion) entrada.readObject();
            switch (peticion.getTipoPeticion()) {
                case MENSAJE:
                    responderPeticion(true);
                    recibirMensajes(peticion.getNumeroMensajes(),false);
                    break;
                case MENSAJEGRUPO:
                    if(comprobarGrupo(peticion.getIdGrupo())){
                        responderPeticion(true);
                        geetInfoGrupo();
                    }else {
                        responderPeticion(false);
                        recibirInfoGrupo(peticion.getIdGrupo());
                    }
                    recibirMensajes(peticion.getNumeroMensajes(),true);
                    break;
                default:
                    Log.e(ERROR, "Peticion invalida");
                    responderPeticion(false);
                    break;
            }

        } catch (IOException e) {
            Log.d(ERROR, e.toString());
            Log.d(ERROR, "Error recibiendo info");

        } catch (ClassNotFoundException e) {
            Log.d(ERROR, "No se puede encontrar la clase peticion");
        }

    }

    /**
     * Envia los datos del usuario para ques sean visibles por otros usuarios
     */
    private void enviarDescubrimiento() {
        Contacto yo = Contacto.getSelf(contexto);
        byte[] imagen = getBytesImagen(yo.getImagen());
        enviar(yo);
        if (imagen != null) {
            enviar(imagen);
            Log.d(IMAGEN, "Enviando bytes de la imagen: " + yo.getImagen());
        }else
            enviar(new byte[0]);
    }

    /**
     * Recibe los datos de otro usuario
     * @return contacto El contacto del descubrimiento o null si no existe
     */
    private Contacto recibirDescubrimiento() {
        try {
            Contacto contacto = (Contacto) entrada.readObject();
            Bitmap imagen = recibirImagen();
            if (imagen != null) {
                String path = guardarImagenContacto(contacto, imagen);
                contacto.setImagen(path);
            }
            contacto.guardar(contexto);
            return contacto;
        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir la respuesta de descubrimiento");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase respuesta");
        }
        return null;
    }

    /**
     * Indica si la solicitud es aceptada
     * @return true si es aceptada, false en caso contrario
     */
    private boolean solicitudAceptada() {
        try {
            RespuestaPeticion respuesta = (RespuestaPeticion) entrada.readObject();
            return respuesta.getTipo().equals(RespuestaPeticion.TipoRespuesta.ACEPTAR);
        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir la respuesta de aceptacion");
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase respuesta");
        }

        return false;
    }

    /**
     * Solicita el envio de mensajes en un grupo
     * @param idGrupo El identificador de grupo
     */
    private void solicitarEnvioMensajesGrupo(String idGrupo){
        Peticion peticion = new Peticion(mensajes.size(),idGrupo);
        Log.e("ENVIO", String.valueOf(mensajes.size()));
        enviar(peticion);
    }

    /**
     * Solicita envio de mensajes
     */
    private void solicitarEnvioMensajes() {
        Peticion peticion = new Peticion(mensajes.size());
        enviar(peticion);
    }

    /**
     * Envia un objeto a través del canal de comunicación
     * @param objeto El objeto a enviar
     */
    private void enviar(Serializable objeto) {
        try {
            salida.writeObject(objeto);
        } catch (IOException e) {
            Log.e(ERROR, "No se puede enviar el objeto");
            e.printStackTrace();
        }
    }

    /**
     * Recibe mensajes y los almacena
     * @param numeroMensajes El numero de mensajes recibidos
     * @param esGrupo Indica si el chat es un grupo
     */
    private void recibirMensajes(int numeroMensajes, boolean esGrupo) {
        try {
            Contacto contacto = recibirDescubrimiento();
            if(!esGrupo)
                nuevoChat(contacto);

            for(int i= 0; i<numeroMensajes; i++) {
                Mensaje mensaje = (Mensaje) entrada.readObject();
                if (null == mensaje.getImagen()) {
                    guardarMensaje(mensaje);
                    notificar(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido());
                } else {
                    Bitmap imagen = recibirImagen();
                    String path = guardarImagenMensaje(mensaje, imagen);
                    mensaje.setImagen(path);
                    guardarMensaje(mensaje);
                    notificar(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido(), imagen);
                }

                notificarMensaje();
            }

        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir el mensaje");
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase mensaje");
        }
    }

    /**
     * Crea un nuevo chat
     * @param contacto El contacto del chat
     */
    private void nuevoChat(Contacto contacto){
        Context context = contexto;
        Chat chat = contacto.getChat(context);
        if(null == chat) {
            chat = new Chat(contacto);
            chat.guardar(context);
        }
        chatConexion = chat;
    }

    /**
     * Almacena un mensaje
     * @param mensaje El mensaje a almacenar
     */
    private void guardarMensaje(Mensaje mensaje){
        Context context = contexto;
            mensaje.registrar(context,chatConexion);
    }

    /**
     * Responde a una petición aceptada o rechazada
     * @param aceptada El valor verdadero o falso de la petición
     */
    private void responderPeticion(boolean aceptada) {
        RespuestaPeticion respuesta;

        if (aceptada) {
            respuesta = new RespuestaPeticion(RespuestaPeticion.TipoRespuesta.ACEPTAR);
        } else {
            respuesta = new RespuestaPeticion(RespuestaPeticion.TipoRespuesta.RECHAZAR);
        }

        enviar(respuesta);
    }

    /**
     * Devuelve los bytes de una imagen
     * @param uri El bitmap de la imagen de perfil
     * @return Los bytes de la imagen o null
     */
    private byte[] getBytesImagen(String uri) {
        try {
            //Obtenemos el bitmap de la imagen de perfil
            Uri uriManual = Uri.parse(uri);
            Log.d(IMAGEN, "La uri es:" + uri);

            ParcelFileDescriptor parcelFileDescriptor = contexto.getContentResolver().openFileDescriptor(uriManual, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor != null ? parcelFileDescriptor.getFileDescriptor() : null;
            if (fileDescriptor == null) {
                return null;
            }
            Bitmap imagen = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();

            return getBytesImagen(imagen);
        } catch (FileNotFoundException e) {
            Log.e(IMAGEN, "El archivo a abrir no existe");
        } catch (IOException e) {
            Log.e(IMAGEN, "Error al obtener la imagen");
        }

        return null;
    }

    /**
     * Devuelve los bytes de una imagen a partir de su bitmap
     * @param imagen Bitmpa de la imagen
     * @return Los bytes de la imagen
     */
    private byte[] getBytesImagen(Bitmap imagen){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Recibe una imagen y la devuelve en Bitmpap
     * @return El Bitmap de la imagen o null
     */
    private Bitmap recibirImagen() {
        try {
            byte[] imagen = (byte[]) entrada.readObject();
            return BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Almacena la imagen de un contacto
     * @param contacto El contacto del que queremos guardar la imagen
     * @param imagen La imagen a almacenar
     * @return El path de la imagen
     */
    private String guardarImagenContacto(Contacto contacto, Bitmap imagen) {
        File file = new File(contexto.getFilesDir(), contacto.getDireccionMac());
        return guardarImagen(file, imagen);
    }

    /**
     * Almacena la imagen de un mensaje
     * @param mensaje El mensaje que contiene la imagen
     * @param imagen La imagen a almacenar
     * @return El path de la imagen
     */
    private String guardarImagenMensaje(Mensaje mensaje, Bitmap imagen){
        File file = new File(contexto.getFilesDir(), mensaje.getEmisor().getDireccionMac() + mensaje.getId());
        return guardarImagen(file,imagen);
    }

    /**
     * Almacena una imagen
     * @param file El fichero en el que se va a guardar
     * @param imagen La imagen a almacenar
     * @return El path de la imagen o null
     */
    private String guardarImagen(File file, Bitmap imagen){
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(this.getBytesImagen(imagen));
            outputStream.close();
            Log.d(IMAGEN, "He guardado en: " + file.getAbsolutePath());
            return file.getAbsolutePath();
         } catch (Exception e) {
            e.printStackTrace();
            }
        return null;
    }

    /**
     * Comprueba si existe un grupo
     * @param id El identificador del grupo
     * @return True si existe, false en caso contrario
     */
    private boolean comprobarGrupo(String id){
        idGrupo = id;
        return Chat.existeGrupo(contexto, id);
    }

    /**
     * Envia la información de un grupo
     * @param id El identificador del grupo
     */
    private void enviarInfoGrupo(String id){
        Chat grupo = Chat.getChatGrupal(contexto, id);
        enviar(grupo != null ? grupo.getNombre() : null);
        enviar((ArrayList) (grupo != null ? grupo.getParticipantes() : null));
    }

    /**
     * Obtiene la información de un grupo
     */
    private void geetInfoGrupo(){
        chatConexion = Chat.getChatGrupal(contexto, idGrupo);
    }

    /**
     * Recibe la información de un grupo
     * @param id El identificador del grupo
     */
    private void recibirInfoGrupo(String id){
        try{
            String nombre = (String)entrada.readObject();
            @SuppressWarnings("unchecked") List<Contacto> participantes = (ArrayList)entrada.readObject();
            participantes.remove(Contacto.getSelf(contexto));
            participantes.add(Contacto.getContacto(contexto, socket.getRemoteDevice()));
            Chat chat = new Chat(id,nombre,participantes);
            chat.guardar(contexto);
            chatConexion = chat;
        }catch (IOException e){
            Log.e(ERROR, "No se han podido recibir los participantes del grupo");
        }catch (ClassNotFoundException e){
            Log.e(ERROR, "Clase no encontrada");
        }

    }

    /**
     * Notifica la recepción de un mensaje
     */
    private void notificarMensaje(){
        Intent intent = new Intent("mensajeNuevo");
        Chat chatActualizado;
        if(chatConexion.esGrupo())
            chatActualizado = Chat.getChatGrupal(contexto,chatConexion.getIdChat());
        else
            chatActualizado = Chat.getChatById(contexto, chatConexion.getIdChat());
        intent.putExtra("chat", chatActualizado);
        LocalBroadcastManager.getInstance(contexto).sendBroadcast(intent);
    }

    /**
     * Muestra una notificación con el mensaje recibido como parámetro
     * @param mensaje El mensaje a mostrar en la notificación
     */
    private void notificar(String mensaje){
        //Intent intent = new Intent(this, NotificationCompat.class);
        Intent intent = new Intent(contexto, ActivityPrincipal.class);
        intent.setAction(ActivityPrincipal.CHATS);
        PendingIntent pIntent = PendingIntent.getActivity(contexto, 0, intent, 0);

        NotificationManager manager = (NotificationManager) contexto.getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notificacion =
                new NotificationCompat.Builder(contexto)
                        .setContentTitle("BlueChat")
                        .setSmallIcon(R.drawable.notificacion_icon)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setContentText(mensaje)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pIntent).build();

        manager.notify(0, notificacion);


    }

    /**
     * Muestra una notificación con el mensaje recibido como parámetro
     * @param mensaje El mensaje a mostrar en la notificación
     */
    private void notificar(String mensaje, Bitmap imagen){
        Intent intent = new Intent(contexto, NotificationCompat.class);
        intent.setAction(ActivityPrincipal.CHATS);
        PendingIntent pIntent = PendingIntent.getActivity(contexto, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager manager = (NotificationManager) contexto.getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notificacion =
                new NotificationCompat.Builder(contexto)
                        .setContentTitle("BlueChat")
                        .setSmallIcon(R.drawable.notificacion_icon)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setLargeIcon(imagen)
                        .setFullScreenIntent(pIntent,true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentText(mensaje).build();

        manager.notify(0,notificacion);

    }

}

