package com.sope.domain.firebase;

import java.util.Map;

public class MessageToUser extends Message {

    private String message;
    private String messageFrom;
    private String targetUserFirebaseToken;

    private static final String MESSAGE_FROM = "messageFrom";
    private static final String MESSAGE_KEY = "message";


    public MessageToUser(String message, String messageFrom, String targetUserFirebaseToken) {
        this.message = message;
        this.targetUserFirebaseToken = targetUserFirebaseToken;
        this.messageFrom = messageFrom;
    }

    public MessageToUser(Map<String, String> messageDataFromFirebase) {
        message = messageDataFromFirebase.get(MESSAGE_KEY);
        messageFrom = messageDataFromFirebase.get(MESSAGE_FROM);


    }

    @Override
    String getTarget() {
        return targetUserFirebaseToken;
    }

    public String getMessage() {
        return message;
    }

    public void setTargetUserFirebaseToken(String targetUserFirebaseToken) {
        this.targetUserFirebaseToken = targetUserFirebaseToken;

    }
    public String getMessageFrom() {
        return messageFrom;
    }

}
