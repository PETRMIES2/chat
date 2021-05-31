package com.sopeapp.utilities;

import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.ChatListDTO;

/**
 * Tyhmästi tehty. Ajateltava loppuun
 */

public class ChatPagination {

    public static boolean hasChats(ChatListDTO channels) {
        return channels != null && channels.getChats() != null && !channels.getChats().isEmpty();
    }

    public static ChatDTO getCurrentChat(ChatListDTO channels) {
        return channels.getChats().get(channels.getSelectedChat());
    }



    public static void setChat(ChatListDTO channels, int index) {
        channels.setSelectedChat(index);
    }

    public static ChatDTO getFirstChat(ChatListDTO channelChats) {
        return channelChats.getChats().get(0);
    }

}
