package com.sope.websocket;

import io.reactivex.disposables.Disposable;

public class DisposableWrapper {
    public final Disposable subscription;
    public final String identification;
    public final DisposableType type;
    public DisposableWrapper(Disposable subscription, String identification, DisposableType type) {
        this.subscription = subscription;
        this.identification = identification;
        this.type = type;
    }
    public static DisposableWrapper buildGeneral(Disposable disposable, String identification) {
        return new DisposableWrapper(disposable, identification, DisposableType.GENERAL);
    }
    public static DisposableWrapper buildChat(Disposable disposable, String identification) {
        return new DisposableWrapper(disposable, identification, DisposableType.CHAT);
    }
    
    public String toString() {
        return String.join(" " ,
                type.toString(),
                ":",
                identification
                );
                
    }
    
}
