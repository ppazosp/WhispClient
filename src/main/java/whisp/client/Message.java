package whisp.client;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {

    private final String sender;
    private final String content;
    private final String receiver;
    private final String time;

    public Message (String sender, String content, String receiver)
    {
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;

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

    public String getTime(){
        return time;
    }
}
