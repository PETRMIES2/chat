package com.sope.domain.firebase;

import java.util.HashMap;

public enum MessageType {

    TOPIC, USER_TO_USER, WARNING, BAN, UNKNOWN;

    private static final HashMap<String, MessageType> CACHE = new HashMap<>();
    static {
        for (MessageType type : MessageType.values()) {
            CACHE.put(type.name(), type);

        }
    }

    public static MessageType get(String possibleMessageType) {
        if (CACHE.containsKey(possibleMessageType)) {
            return CACHE.get(possibleMessageType);
        }
        return UNKNOWN;
    }
}
