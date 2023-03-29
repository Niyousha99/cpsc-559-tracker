package Tracker.Infrastructure.Election;

public class ElectionMessage {
    private final MessageType messageType;
    private final String process;
    private final String data;

    public ElectionMessage(MessageType messageType, String process) {
        this.messageType = messageType;
        this.process = process;
        this.data = null;
    }

    public ElectionMessage(MessageType messageType, String process, String data) {
        this.messageType = messageType;
        this.process = process;
        this.data = data;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public String getProcess() {
        return this.process;
    }

    public String getData()
    {
        return data;
    }
}
