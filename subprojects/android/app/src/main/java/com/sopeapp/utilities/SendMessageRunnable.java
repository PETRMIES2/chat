package com.sopeapp.utilities;

import android.util.Log;

import com.sopeapp.SopeApplication;
import com.sopeapp.api.ChatService;
import com.sope.domain.firebase.MessageToChat;

import java.io.IOException;

import retrofit2.Call;

public class SendMessageRunnable implements Runnable {
    private MessageToChat message;

    public SendMessageRunnable(MessageToChat message) {
        this.message = message;
    }

    @Override
    public void run() {

        try {
            ChatService chatService = SopeApplication.getInstance().getRetrofit().create(ChatService.class);
            Call<Void> query = chatService.sendMessage(message);
            query.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("POSTING MESSAGE ERROR", e.getMessage());
        }

    }
}
