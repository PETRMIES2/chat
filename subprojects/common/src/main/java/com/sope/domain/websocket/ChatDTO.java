package com.sope.domain.websocket;

public class ChatDTO {

    public Long chatNumber;
    public String chatHeader;

    public ChatDTO(final Long chatNumber, final String chatHeader) {
        this.chatNumber = chatNumber;
        this.chatHeader = chatHeader;
    }

    public ChatDTO(final String chatNumber, final String chatHeader) {
        this(Long.valueOf(chatNumber), chatHeader);
    }
}
