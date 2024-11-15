package whisp.client;

import java.io.Serializable;
import java.util.ArrayList;

public class Friend {
    private boolean connected;
    private final ArrayList<Message> chat;

    public Friend()
    {
        connected = true;
        chat = new ArrayList<>();
    }

    public void addMessage(Message m) {
        chat.add(m);
    }

    public ArrayList<Message> getChat() {
        return chat;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
