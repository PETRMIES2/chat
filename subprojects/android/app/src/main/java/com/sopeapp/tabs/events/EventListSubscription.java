package com.sopeapp.tabs.events;

import android.util.Log;

import com.sope.domain.websocket.SopeSocketMessage;

import rx.Subscriber;


public class EventListSubscription extends Subscriber<SopeSocketMessage> {
    private EventFragment fragment;

    public EventListSubscription(EventFragment fragment) {
        this.fragment = fragment;
    }

    public void onCompleted() {
        Log.d("General", "onCompleted()");
    }

    public void onError(Throwable e) {
        Log.e("General", "onError()", e);
    }

    public void onNext(SopeSocketMessage socketMessage) {
        if (socketMessage.messageType.isEventList()) {
            fragment.getEvents().clear();
            fragment.getEvents().addAll(socketMessage.categoryList);
            fragment.getActivity().runOnUiThread(() -> fragment.getEventDatamanager().notifyDataSetChanged());
        }


    }
}
