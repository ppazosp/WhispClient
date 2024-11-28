package whisp.client.gui.entities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un mensaje en una conversación de chat.
 * <p>
 * Un mensaje contiene información sobre el remitente, el contenido, el destinatario,
 * el tipo de contenido (texto u otro formato) y la hora en que fue enviado.
 */
public class Message {

    private final String sender;
    private final String content;
    private final String receiver;
    private final String time;
    private final boolean isText;

    /**
     * Crea un nuevo mensaje.
     *
     * @param sender el nombre de usuario del remitente del mensaje.
     * @param content el contenido del mensaje.
     * @param receiver el nombre de usuario del destinatario del mensaje.
     * @param isText {@code true} si el mensaje es de tipo texto; {@code false} si es de otro tipo.
     */
    public Message(String sender, String content, String receiver, boolean isText) {
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;
        this.isText = isText;

        // Asigna la hora actual en formato HH:mm
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.time = now.format(formatter);
    }

    /**
     * Devuelve el contenido del mensaje.
     *
     * @return el contenido del mensaje como una cadena.
     */
    public String getContent() {
        return content;
    }

    /**
     * Devuelve el nombre de usuario del remitente del mensaje.
     *
     * @return el nombre de usuario del remitente.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Devuelve el nombre de usuario del destinatario del mensaje.
     *
     * @return el nombre de usuario del destinatario.
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Indica si el mensaje es de tipo texto.
     *
     * @return {@code true} si el mensaje es de tipo texto; {@code false} en caso contrario.
     */
    public boolean isText() {
        return isText;
    }

    /**
     * Devuelve la hora en la que el mensaje fue enviado.
     * <p>
     * La hora está formateada en el formato "HH:mm".
     *
     * @return la hora del mensaje como una cadena.
     */
    public String getTime() {
        return time;
    }
}