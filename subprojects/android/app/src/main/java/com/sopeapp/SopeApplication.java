package com.sopeapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import com.sopeapp.websocket.WebSocketService;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;


public class SopeApplication extends Application {
    private static SopeApplication instance;
    private Properties applicationProperties;

    private RestComponent restComponent;
    private BehaviorSubject messageSubject;
    private WebSocketService webSocketService;
    private PublishSubject categoryChatSubscription;

    private ReplaySubject tabSubscription;

    @Inject
    Retrofit retrofit;

    public static SopeApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public BehaviorSubject messageSubscription() {
        return messageSubject;
    }

    public ReplaySubject getTabSubscription() {
        return tabSubscription;
    }

    public static class BackgroundThread extends HandlerThread {
        public BackgroundThread() {
            super("SopeSchedules-BackgroundThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    private ServiceConnection serviceConnector = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,IBinder service) {
            WebSocketService.WebSocketBinder binder = (WebSocketService.WebSocketBinder) service;
            webSocketService = binder.getService(); //<--------- from here on can access service!
        }

        public void onServiceDisconnected(ComponentName arg0) {
            webSocketService = null;
        }

    };

    public PublishSubject getCategoryChatSubscription() {
        return categoryChatSubscription;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        messageSubject = BehaviorSubject.create();
        messageSubject.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        categoryChatSubscription = PublishSubject.create();
        categoryChatSubscription.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());


        tabSubscription = ReplaySubject.create();
        tabSubscription.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        applicationProperties = new Properties();
        try {
            applicationProperties.load(getAppContext().getAssets().open("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        bindService(new Intent(this,WebSocketService.class), serviceConnector, Context.BIND_AUTO_CREATE);
        restComponent = DaggerRestComponent.builder()
                // FIXME
                .applicationModule(new ApplicationModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .restApiModule(new RestApiModule(getServerUrl()))
                .build();

        getRestComponent().inject(instance);


    }

    public WebSocketService getWebSocketService() {
        if (webSocketService == null) {
            bindService(new Intent(this,WebSocketService.class), serviceConnector, Context.BIND_AUTO_CREATE);
        }
        return webSocketService;
    }

    private String getProperty(String name) {
        return applicationProperties.getProperty(name, "CANNOT_FIND_KEY " + name);
    }
    public String getWebsocketUrl() {
        return getProperty("websocket");
    }

    public String getServerUrl() {
        return getProperty("server");
    }


    public RestComponent getRestComponent() {
        return restComponent;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public Looper getBackgroundLooper() {
        SopeApplication.BackgroundThread backgroundThread = new SopeApplication.BackgroundThread();
        backgroundThread.start();
        return backgroundThread.getLooper();

    }
}
