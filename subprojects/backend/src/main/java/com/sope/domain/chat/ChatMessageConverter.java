package com.sope.domain.chat;

import com.google.common.base.Function;
import com.sope.domain.firebase.MessageToChat;

public class ChatMessageConverter implements Function<ChatMessage, MessageToChat> {

    @Override
    public MessageToChat apply(final ChatMessage chatMessage) {
        return new MessageToChat(chatMessage.getMessage(), chatMessage.getUsername(), chatMessage.getSendTime(), chatMessage.getChat().getChatNumber(), chatMessage.getChat().getHeader());
    }

}
