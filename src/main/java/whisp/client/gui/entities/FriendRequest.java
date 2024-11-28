package whisp.client.gui.entities;

/**
 * Representa una solicitud de amistad en la aplicación.
 * <p>
 * Cada solicitud contiene información sobre el usuario que la envía y el usuario que la recibe.
 */
public class FriendRequest {

    private final String senderUsername;
    private final String receiverUsername;

    /**
     * Crea una nueva solicitud de amistad.
     *
     * @param senderUsername el nombre de usuario del remitente de la solicitud.
     * @param receiverUsername el nombre de usuario del destinatario de la solicitud.
     */
    public FriendRequest(String senderUsername, String receiverUsername) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    /**
     * Devuelve el nombre de usuario del destinatario de la solicitud.
     *
     * @return el nombre de usuario del destinatario.
     */
    public String getReceiverUsername() {
        return receiverUsername;
    }

    /**
     * Devuelve el nombre de usuario del remitente de la solicitud.
     *
     * @return el nombre de usuario del remitente.
     */
    public String getSenderUsername() {
        return senderUsername;
    }
}