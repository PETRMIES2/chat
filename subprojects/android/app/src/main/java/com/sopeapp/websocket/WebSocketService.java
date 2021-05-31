package com.sopeapp.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sopeapp.SopeApplication;
import com.sopeapp.api.AuthHeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.logging.HttpLoggingInterceptor;

public class WebSocketService extends Service {


    //https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/WebSocketEcho.java
    private final OkHttpClient.Builder httpClient;

    {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.writeTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(10, TimeUnit.SECONDS);
    }

    private WebSocket webSocket;
    private WebSocketBinder websocketBinder = new WebSocketBinder();
    private OkHttpClient okHttpClient;
    private Gson gson;

    public WebSocketService() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return websocketBinder;
    }

    @Override
    public void onCreate() {
        gson = provideGson();
        okHttpClient =  httpClient.addInterceptor(new AuthHeaderInterceptor()).build();
        createWebSocket();
    }

    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.setLenient();
        return gsonBuilder.create();
    }

    private void createWebSocket() {
        Request request = new Request.Builder()
                .url(SopeApplication.getInstance().getWebsocketUrl())
                .build();
        webSocket = okHttpClient.newWebSocket(request, new SopeWebSocketListener(gson));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("wsService", "destroy websocket");
        if (webSocket != null) {
            webSocket.cancel();
            webSocket = null;
        }
    }

    public void sendMessage(String message) {
        boolean isSocketOpen = webSocket.send(message);
        if (!isSocketOpen) {
            createWebSocket();
            webSocket.send(message);
        }
    }
    public void sendMessage(Object jsonObject) {
        if (gson == null) {
            provideGson();
        }
        if (jsonObject != null) {
            sendMessage(gson.toJson(jsonObject));
        }
    }
    public class WebSocketBinder extends android.os.Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

}
