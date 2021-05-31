package com.sopeapp.chat;

import com.sope.domain.firebase.MessageToChat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ChatMessageContainer {

    public static final HashMap<Long, List<MessageToChat>> messages = new HashMap<>();

    public static void emptyAllMessages() {
        messages.clear();
    }

    public static void postMessage(MessageToChat messageToChat) {
        Long chatNumber = messageToChat.getChatNumber();
        if (chatNumber != null) {
            List<MessageToChat> chatMessages;
            if (messages.containsKey(chatNumber)) {
                chatMessages = messages.get(chatNumber);
            } else {
                chatMessages = new LinkedList<>();
                messages.put(chatNumber, chatMessages);
            }
            chatMessages.add(messageToChat);
        }

    }

    public static List<MessageToChat> getAllMessagesToChat(Long chatNumber) {
        if (messages.containsKey(chatNumber)) {
            return messages.get(chatNumber);
        }
        LinkedList<MessageToChat> chatMessages = new LinkedList<>();
        messages.put(chatNumber, chatMessages);


        return chatMessages;
    }

}
