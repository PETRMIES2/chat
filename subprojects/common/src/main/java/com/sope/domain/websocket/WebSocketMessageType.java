package com.sope.domain.websocket;

public enum WebSocketMessageType {
    GENERAL_LIST,
    EVENTS_LIST,
    POPULAR_LIST,
    TVSHOWS_LIST,
    MESSAGET_TO_CHAT,
    JOIN_CHAT,
    USER_JOINED_CHAT,
    USER_LEFT_CHAT,
    CHAT_USERLIST_UPDATE,
    GET_OLD_MESSAGES,
    GET_GENERAL_CHATS,
    GET_EVENTS_CHATS,
    GET_TVSHOW_CHATS,
    RENAME_CHAT,
    CREATE_CHAT,
    CHAT_CREATED,
    NEXT_CHAT;


    public boolean isRenameChat() {
        return this == RENAME_CHAT;
    }
    public boolean isCreateChat() {
        return this == CREATE_CHAT;
    }
    public boolean isNextChat() {
        return this == NEXT_CHAT;
    }
    public boolean isGetGeneralChats() {
        return this == GET_GENERAL_CHATS;
    }
    public boolean isGetEventChats() {
        return this == GET_EVENTS_CHATS;
    }
    public boolean isGetTvShowChats() {
        return this == GET_TVSHOW_CHATS;
    }

    public boolean isUserJoined() {
        return this == USER_JOINED_CHAT;
    }
    public boolean isGetOldMessages() {
        return this == GET_OLD_MESSAGES;
    }
    public boolean isGeneralList() {
        return this == GENERAL_LIST;
    }
    public boolean isJoinChat() {
        return this == JOIN_CHAT;
    }
    public boolean isRemoveUserFromChat() {
        return this == USER_LEFT_CHAT;
    }
    public boolean isMessageToChat() {
        return this == MESSAGET_TO_CHAT;
    }

    public boolean isEventList() {
        return this == EVENTS_LIST;
    }
    public boolean isTvShowList() {
        return this == TVSHOWS_LIST;
    }
    public boolean isPopularList() {
        return this == POPULAR_LIST;
    }

    public boolean isUserListUpdate() {
        return this == CHAT_USERLIST_UPDATE;
    }

    public boolean isChatCreated() {
        return this == CHAT_CREATED;
    }
}
