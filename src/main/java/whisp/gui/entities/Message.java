package whisp.gui.entities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {

    private final String sender;
    private final String content;
    private final String receiver;
    private final String time;
    private final boolean isText;

    public Message (String sender, String content, String receiver, boolean isText)
    {
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;
        this.isText = isText;

        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.time =  now.format(formatter);
    }
    public String getContent() {
        return content;
    }
    public String getSender() {
        return sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public boolean isText() {
        return isText;
    }
    public String getTime(){
        return time;
    }
}
