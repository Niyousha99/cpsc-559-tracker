package Tracker.Infrastructure.Election;

public class ElectionMessage {
    private final MessageType messageType;
    private final String process;

    public ElectionMessage(MessageType messageType, String process) {
        this.messageType = messageType;
        this.process = process;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public String getProcess() {
        return this.process;
    }
    
}
