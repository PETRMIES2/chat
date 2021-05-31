package com.sope.domain.firebase;

public class MessageBase<T extends Message> {

    private static final String TOPICS_PREFIX = "/topics/";
    private String to;
    private T data;

    public MessageBase(T message) {
        this.data = message;
    }

    public T getData() {
        return data;
    }
    
    public MessageBase toChannel() {
        this.to = TOPICS_PREFIX + data.getTarget();
        this.data.setMessageType(MessageType.TOPIC);
        return this;
    }
    public MessageBase toUser() {
        this.to = data.getTarget();
        this.data.setMessageType(MessageType.USER_TO_USER);
        return this;
    }


}

