package Tracker.Infrastructure.Election;

public record ElectionMessage(MessageType messageType, String process, String data)
{}
