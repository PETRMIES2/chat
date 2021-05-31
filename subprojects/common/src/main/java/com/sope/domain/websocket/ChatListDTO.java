package com.sope.domain.websocket;

import java.util.LinkedList;
import java.util.List;

public class ChatListDTO {
    private final List<ChatDTO> chats = new LinkedList<>();
    private int selectedChat = 0;

    public ChatListDTO() {
    }

    public ChatListDTO(final List<ChatDTO> chatList) {
        chats.addAll(chatList);
    }
    public void setChats(final List<ChatDTO> channelChats) {
        chats.addAll(channelChats);
    }

    public List<ChatDTO> getChats() {
        return chats;
    }

    public int getSelectedChat() {
        return selectedChat;
    }

    public void setSelectedChat(final int selectedChat) {
        this.selectedChat = selectedChat;
    }

}
