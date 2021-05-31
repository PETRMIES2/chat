package com.sopeapp.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.SopeApplication;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SopeWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private Gson gson;

    public SopeWebSocketListener(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
/*        webSocket.send("Knock, knock!");
        webSocket.send("Hello!");
        webSocket.send(ByteString.decodeHex("deadbeef"));
        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
        */
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        SopeSocketMessage socketMessage = gson.fromJson(text, SopeSocketMessage.class);
        Log.d(this.getClass().getSimpleName(), "Receiving messsage " + socketMessage.messageType);
//        System.out.println("********** Receiving: " + text);
        if (socketMessage.messageType.isGetTvShowChats() ||
                socketMessage.messageType.isGetEventChats() ||
                socketMessage.messageType.isGetGeneralChats()) {
            SopeApplication.getInstance().getCategoryChatSubscription().onNext(socketMessage);

        } else {
            SopeApplication.getInstance().getTabSubscription().onNext(socketMessage);

        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("********** Receiving - bytes: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        System.out.println("********** Closing: websocket " + code + " " + reason);
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("********** Closed: websocket " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("FAILURE websocket");

        // Tälle controller, johon voidaan lähettää virheviestit
        t.printStackTrace();
    }
}
