package com.sope.domain.firebase;

public abstract class Message {
    private MessageType messageType;
    public static final String MESSAGETYPE_TYPE = "messageType";

    abstract String getTarget();

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

}
