package com.sopeapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sope.domain.user.UserDTO;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.ChatListDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.utilities.ChatPagination;

//Hitaita pyyntöjä. Korjaa kun olet saanut kaiken toimimaan.
public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "SOPE_SETTINGS";
    private static final String USER_JSON = "USER";
    private static final String CHANNEL_WITH_CHATS = "SHOW";
    private static final String CHAT_USER_COUNT = "CHAT_USER_COUNT";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    // FIXME gson. Should user restApiMOduleGson
    public static UserDTO getUser(Context context) {
        return new Gson().fromJson(getSharedPreferences(context).getString(USER_JSON, null), UserDTO.class);
    }

    public static void saveUser(Context context, UserDTO user) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_JSON, new Gson().toJson(user));
        editor.commit();
    }


    private static void updateSopeMessage(Context context, SopeSocketMessage chat) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CHANNEL_WITH_CHATS, new Gson().toJson(chat));
        editor.commit();
    }

    public static SopeSocketMessage getSopeMessage(Context context) {
        return new Gson().fromJson(getSharedPreferences(context).getString(CHANNEL_WITH_CHATS, null), SopeSocketMessage.class);
    }
    public static void updateSocketMessage(Context context,SopeSocketMessage socketMessage) {
        updateSopeMessage(context, socketMessage);
    }

    public static ChatDTO getCurrentChat(Context context) {
        ChatListDTO channelInformation = getSopeMessage(context).chatList;
        return ChatPagination.getCurrentChat(channelInformation);    }

    public static void updateChatUserCount(Context context, int usersInChat) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(CHAT_USER_COUNT, usersInChat);
        editor.commit();

    }
    public static int getChatUserCount(Context context) {
        return getSharedPreferences(context).getInt(CHAT_USER_COUNT,0);
    }

}
