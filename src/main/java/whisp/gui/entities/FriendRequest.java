package whisp.gui.entities;

public class FriendRequest {

    String senderUsername;
    String receiverUsername;

    public FriendRequest(String senderUsername, String receiverUsername){
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }
    public String getSenderUsername() {
        return senderUsername;
    }
}
