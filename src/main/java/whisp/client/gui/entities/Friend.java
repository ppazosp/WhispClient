package whisp.client.gui.entities;

import java.util.ArrayList;

/**
 * Representa un amigo en una aplicación de chat.
 * <p>
 * Cada amigo tiene un estado de conexión, un historial de mensajes
 * y un indicador para saber si tiene mensajes no leídos.
 */
public class Friend {
    private boolean connected;
    private final ArrayList<Message> chat;
    private boolean messagesNotRead;

    /**
     * Crea una nueva instancia de un amigo con el estado de conexión activo
     * y sin mensajes en el historial.
     */
    public Friend() {
        connected = true;
        chat = new ArrayList<>();
        messagesNotRead = false;
    }

    /**
     * Añade un mensaje al historial de chat del amigo y marca que hay mensajes no leídos.
     *
     * @param m el mensaje a añadir al historial de chat.
     */
    public void addMessage(Message m) {
        chat.add(m);
        messagesNotRead = true;
    }

    /**
     * Devuelve el historial de chat del amigo.
     *
     * @return una lista de mensajes del chat.
     */
    public ArrayList<Message> getChat() {
        return chat;
    }

    /**
     * Verifica si el amigo está conectado.
     *
     * @return {@code true} si el amigo está conectado; {@code false} en caso contrario.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Establece el estado de conexión del amigo.
     *
     * @param connected {@code true} para marcar al amigo como conectado; {@code false} en caso contrario.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Indica si el amigo tiene mensajes no leídos.
     *
     * @return {@code true} si hay mensajes no leídos; {@code false} en caso contrario.
     */
    public boolean hasMessages() {
        return messagesNotRead;
    }

    /**
     * Marca todos los mensajes del amigo como leídos.
     */
    public void readMessage() {
        messagesNotRead = false;
    }
}
