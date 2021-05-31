package com.sope.domain.websocket;

import java.util.List;

import com.sope.domain.firebase.MessageToChat;

public class SopeSocketMessage {

    public final WebSocketMessageType messageType;
    public String username;
    public CategoryDTO category;
    public ChatDTO chat;

    public List<CategoryDTO> categoryList;
    public List<MessageToChat> oldMessages;
    public List<String> usersInChat;

    public MessageToChat messageToChat;

    public ChatListDTO chatList;

    public SopeSocketMessage(final WebSocketMessageType messageType) {
        this.messageType = messageType;
    }

}
