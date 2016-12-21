package asimov.uva.es.bluechat;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
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

import asimov.uva.es.bluechat.Dominio.Chat;
import asimov.uva.es.bluechat.Dominio.Contacto;
import asimov.uva.es.bluechat.Dominio.Mensaje;
import asimov.uva.es.bluechat.Dominio.PaquetesBluetooth.Peticion;
import asimov.uva.es.bluechat.Dominio.PaquetesBluetooth.RespuestaPeticion;

/**
 * Hilo encargado de la transmisión de los mensajes una vez se ha establecido la conexión
 * @author David Robles Gallardo
 * @author Silvia Arias Herguedas
 * @author Hector Del Campo Pando
 * @author Alberto Gutierrez Perez
 */
public class ConexionBluetooth extends Thread {

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

    private Chat chatConexion;

    private final String ERROR = "ERROR";
    private final String CONEXION = "CONEXION";

    private final BluetoothSocket socket;
    private List<Mensaje> mensajes = null;

    private final String IMAGEN = "Imagen";


    //TODO enviar idgrupo de forma correcta
    private String idGrupo;
    public void setIdGrupo(String idGrupo){this.idGrupo = idGrupo;}

    /**
     * Modo de ejecucion
     */
    private final Modo modo;

    /**
     * Inicaliza los streams de la conexión bluetooth
     * a partir del socket de la misma
     *
     * @param socket El socket de la conexión
     * @param modo   de ejecucion
     */
    public ConexionBluetooth(BluetoothSocket socket, Modo modo) {
        Log.d(CONEXION, "CONEXION BUENA");
        this.socket = socket;
        this.modo = modo;

        try {
            /* Primero, crear siempre el output stream, sino se producira un bloqueo indefinido.
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
     * Inicaliza los streams de la conexión bluetooth
     * a partir del socket de la misma
     *
     * @param socket  El socket de la conexión
     * @param modo    de ejecucion
     * @param mensaje a enviar
     */
    public ConexionBluetooth(BluetoothSocket socket, Modo modo, List<Mensaje> mensaje) {
        this(socket, modo);
        this.mensajes = mensaje;
    }

    /**
     * Recibe el mensaje
     */
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
     * Envia mensajes a un servidor
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

    private void enviarMensajes(){
        for(Mensaje mensaje : mensajes) {
            enviar(mensaje);
            if (null != mensaje.getImagen()) {
                byte[] imagen = getBytesImagen(mensaje.getImagen());
                enviar(imagen);
            }
            mensaje.marcarEnviado(socket.getRemoteDevice().getAddress());
        }
    }

    /**
     * Recibe peticiones y actua en consecuencia
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

    private void enviarDescubrimiento() {
        Contacto yo = Contacto.getSelf();
        byte[] imagen = getBytesImagen(yo.getImagen());
        enviar(yo);
        if (imagen != null) {
            enviar(imagen);
            Log.d(IMAGEN, "Enviando bytes de la imagen: " + yo.getImagen());
        }else
            enviar(new byte[0]);
    }

    private Contacto recibirDescubrimiento() {
        try {
            Contacto contacto = (Contacto) entrada.readObject();
            Bitmap imagen = recibirImagen();
            if (imagen != null) {
                String path = guardarImagenContacto(contacto, imagen);
                contacto.setImagen(path);
            }
            contacto.guardar(MainActivity.getMainActivity());
            return contacto;
        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir la respuesta de descubrimiento");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase respuesta");
        }
        return null;
    }

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

    private void solicitarEnvioMensajesGrupo(String idGrupo){
        Peticion peticion = new Peticion(mensajes.size(),idGrupo);
        Log.e("ENVIO", String.valueOf(mensajes.size()));
        enviar(peticion);
    }

    private void solicitarEnvioMensajes() {
        Peticion peticion = new Peticion(mensajes.size());
        enviar(peticion);
    }

    private void enviar(Serializable objeto) {
        try {
            salida.writeObject(objeto);
        } catch (IOException e) {
            Log.e(ERROR, "No se puede enviar el objeto");
            e.printStackTrace();
        }
    }

    private void recibirMensajes(int numeroMensajes, boolean esGrupo) {
        try {
            Contacto contacto = recibirDescubrimiento();
            if(!esGrupo)
                nuevoChat(contacto);

            for(int i= 0; i<numeroMensajes; i++) {
                Mensaje mensaje = (Mensaje) entrada.readObject();
                if (null == mensaje.getImagen()) {
                    guardarMensaje(mensaje);
                    MainActivity.getMainActivity().notificar(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido());
                } else {
                    Bitmap imagen = recibirImagen();
                    String path = guardarImagenMensaje(mensaje, imagen);
                    mensaje.setImagen(path);
                    guardarMensaje(mensaje);
                    MainActivity.getMainActivity().notificar(mensaje.getEmisor().getNombre() + ": " + mensaje.getContenido(), imagen);
                }

                notificarMensaje();
            }

        } catch (IOException e) {
            Log.e(ERROR, "No se puede recibir el mensaje");
        } catch (ClassNotFoundException e) {
            Log.e(ERROR, "No se puede encontrar la clase mensaje");
        }
    }

    private void nuevoChat(Contacto contacto){
        Context context = MainActivity.getMainActivity();
        Chat chat = contacto.getChat(context);
        if(null == chat) {
            chat = new Chat(contacto);
            chat.guardar(context);
        }
        chatConexion = chat;
    }

    private void guardarMensaje(Mensaje mensaje){
        Context context = MainActivity.getMainActivity();
            mensaje.registrar(context,chatConexion);
    }

    private void responderPeticion(boolean aceptada) {
        RespuestaPeticion respuesta;

        if (aceptada) {
            respuesta = new RespuestaPeticion(RespuestaPeticion.TipoRespuesta.ACEPTAR);
        } else {
            respuesta = new RespuestaPeticion(RespuestaPeticion.TipoRespuesta.RECHAZAR);
        }

        enviar(respuesta);
    }

    private byte[] getBytesImagen(String uri) {
        try {
            //Obtenemos el bitmap de la imagen de perfil
            Uri uriManual = Uri.parse(uri);
            Log.d(IMAGEN, "La uri es:" + uri);

            ParcelFileDescriptor parcelFileDescriptor = MainActivity.getMainActivity().getContentResolver().openFileDescriptor(uriManual, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
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

    private byte[] getBytesImagen(Bitmap imagen){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap recibirImagen() {
        try {
            byte[] imagen = (byte[]) entrada.readObject();
            return BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String guardarImagenContacto(Contacto contacto, Bitmap imagen) {
        File file = new File(MainActivity.getMainActivity().getFilesDir(), contacto.getDireccionMac());
        return guardarImagen(file, imagen);
    }

    private String guardarImagenMensaje(Mensaje mensaje, Bitmap imagen){
        File file = new File(MainActivity.getMainActivity().getFilesDir(), mensaje.getEmisor().getDireccionMac() + mensaje.getId());
        return guardarImagen(file,imagen);
    }

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

    private boolean comprobarGrupo(String id){
        idGrupo = id;
        return Chat.existeGrupo(MainActivity.getMainActivity(), id);
    }

    private void enviarInfoGrupo(String id){
        Chat grupo = Chat.getChatGrupal(MainActivity.getMainActivity(), id);
        enviar(grupo.getNombre());
        enviar((ArrayList)grupo.getParticipantes());
    }

    private void geetInfoGrupo(){
        chatConexion = Chat.getChatGrupal(MainActivity.getMainActivity(),idGrupo);
    }

    private void recibirInfoGrupo(String id){
        try{
            String nombre = (String)entrada.readObject();
            List<Contacto> participantes = (ArrayList)entrada.readObject();
            participantes.remove(Contacto.getSelf());
            participantes.add(Contacto.getContacto(MainActivity.getMainActivity(), socket.getRemoteDevice()));
            Chat chat = new Chat(id,nombre,participantes);
            chat.guardar(MainActivity.getMainActivity());
            chatConexion = chat;
        }catch (IOException e){
            Log.e(ERROR, "No se han podido recibir los participantes del grupo");
        }catch (ClassNotFoundException e){
            Log.e(ERROR, "Clase no encontrada");
        }

    }

    private void notificarMensaje(){
        Context context = MainActivity.getMainActivity();
        Intent intent = new Intent("mensajeNuevo");
        Chat chatActualizado;
        if(chatConexion.esGrupo())
            chatActualizado = Chat.getChatGrupal(context,chatConexion.getIdChat());
        else
            chatActualizado = Chat.getChatById(context, chatConexion.getIdChat());
        intent.putExtra("chat", chatActualizado);
        LocalBroadcastManager.getInstance(MainActivity.getMainActivity()).sendBroadcast(intent);
    }

}

