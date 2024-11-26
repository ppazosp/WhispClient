package whisp.client.gui.entities;

import java.util.ArrayList;

public class Friend {
    private boolean connected;
    private final ArrayList<Message> chat;
    private boolean messagesNotRead;

    public Friend()
    {
        connected = true;
        chat = new ArrayList<>();
        messagesNotRead = false;
    }

    public void addMessage(Message m) {
        chat.add(m);
        messagesNotRead = true;
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

    public boolean hasMessages() {
        return messagesNotRead;
    }

    public void readMessage(){
        messagesNotRead = false;
    }
}
