package com.sopeapp.websocket;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sope.domain.firebase.MessageToChat;
import com.sope.domain.user.UserDTO;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sope.domain.websocket.WebSocketMessageType;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;
import com.sopeapp.utilities.ChatPagination;

import java.util.Date;


public class WebSocketMessageManager {

    public static void subscribeToChat(int position, Context context) {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(context);
        unsubscribeChat(ChatPagination.getCurrentChat(sopeMessage.chatList), sopeMessage.category);

        ChatPagination.setChat(sopeMessage.chatList, position);

        SharedPreferencesManager.updateSocketMessage(context, sopeMessage);

        final ChatDTO currentChat = ChatPagination.getCurrentChat(sopeMessage.chatList);
        joinChat(currentChat, sopeMessage.category);
    }

    public static void joinChat(ChatDTO chatToJoin, CategoryDTO category) {
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());
        FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(chatToJoin.chatNumber));

        SopeSocketMessage addUserToChat = new SopeSocketMessage(WebSocketMessageType.JOIN_CHAT);
        addUserToChat.chat = chatToJoin;
        addUserToChat.username = user.getUsername();
        addUserToChat.category = category;
        addUserToChat.messageToChat = new MessageToChat("Käyttäjä " + user.getUsername() + " liittyi keskusteluun ", "SOPE", new Date(), chatToJoin.chatNumber, chatToJoin.chatHeader);
        SopeApplication.getInstance().getWebSocketService().sendMessage(addUserToChat);
    }
    public static void getOldMessages(ChatDTO currentChat) {
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());

        SopeSocketMessage addUserToChat = new SopeSocketMessage(WebSocketMessageType.GET_OLD_MESSAGES);
        addUserToChat.chat = currentChat;
        addUserToChat.username = user.getUsername();
        SopeApplication.getInstance().getWebSocketService().sendMessage(addUserToChat);
    }


    public static void unsubscribeChat(ChatDTO currentChat, CategoryDTO category) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(currentChat.chatNumber));

        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());

        SopeSocketMessage removeUserFromChat = new SopeSocketMessage(WebSocketMessageType.USER_LEFT_CHAT);
        removeUserFromChat.chat = currentChat;
        removeUserFromChat.category = category;
        removeUserFromChat.username = user.getUsername();
        removeUserFromChat.messageToChat = new MessageToChat("Käyttäjä " + user.getUsername() + " poistui keskustelusta ", "SOPE", new Date(), currentChat.chatNumber, currentChat.chatHeader);
        //FIXME create subscriber that websocket handles eli kuuntelija, jota socket-service kuuntelee. Tällöin ei tarvitse lähetellä suoraan
        // tietoja socketille.
        SopeApplication.getInstance().getWebSocketService().sendMessage(removeUserFromChat);

    }

    public static void getTabList(WebSocketMessageType category) {
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());

        SopeSocketMessage chatsForCategory = new SopeSocketMessage(category);
        chatsForCategory.username = user.getUsername();
        SopeApplication.getInstance().getWebSocketService().sendMessage(chatsForCategory);
    }
    public static void getChatsFor(CategoryDTO chatCategoryDTO, WebSocketMessageType category) {
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());

        SopeSocketMessage chatsForCategory = new SopeSocketMessage(category);
        chatsForCategory.username = user.getUsername();
        chatsForCategory.category = chatCategoryDTO;

        SopeApplication.getInstance().getWebSocketService().sendMessage(chatsForCategory);
    }
    public static void renameChatHeader(String newChatHeader, Long chatNumber) {
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());

        SopeSocketMessage renameChatHeader = new SopeSocketMessage(WebSocketMessageType.RENAME_CHAT);
        renameChatHeader.username = user.getUsername();
        renameChatHeader.chat = new ChatDTO(chatNumber,newChatHeader);

        SopeApplication.getInstance().getWebSocketService().sendMessage(renameChatHeader);
    }

    public static void createChat(String newChatName) {
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());

        SopeSocketMessage createChat = new SopeSocketMessage(WebSocketMessageType.CREATE_CHAT);
        createChat.username = user.getUsername();
        createChat.chat = ChatPagination.getCurrentChat(sopeMessage.chatList);
        createChat.category = sopeMessage.category;
        createChat.chat = new ChatDTO(0L, newChatName);

        unsubscribeChat(ChatPagination.getCurrentChat(sopeMessage.chatList), sopeMessage.category);

        //Kompleksinen. Lähetetään taustalle create chat, joka luo chätin ja lähettää takaisin CHAT_CREATED-viestin, jonka FirebaseSubscribee
        SopeApplication.getInstance().getWebSocketService().sendMessage(createChat);

    }


}
