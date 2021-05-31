package com.sopeapp.tabs.popular;

import android.util.Log;

import com.sope.domain.websocket.SopeSocketMessage;

import rx.Subscriber;


public class PopularListSubscription extends Subscriber<SopeSocketMessage> {
    private PopularFragment fragment;

    public PopularListSubscription(PopularFragment fragment) {
        this.fragment = fragment;
    }

    public void onCompleted() {
        Log.d("Popular", "onCompleted()");
    }

    public void onError(Throwable e) {
        Log.e("Popular", "onError()", e);
    }

    public void onNext(SopeSocketMessage socketMessage) {
        if (socketMessage.messageType.isPopularList()) {
            fragment.getPopularCategories().clear();
            fragment.getPopularCategories().addAll(socketMessage.categoryList);
            fragment.getActivity().runOnUiThread(() -> fragment.getPopularDatamanager().notifyDataSetChanged());
        }


    }
}
