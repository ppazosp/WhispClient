package whisp.client;

public class Message {

    private final String sender;
    private final String content;
    private final String receiver;

    public Message (String sender, String content, String receiver)
    {
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;
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
}
