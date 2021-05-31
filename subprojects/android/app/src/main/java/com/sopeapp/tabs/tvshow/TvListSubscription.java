package com.sopeapp.tabs.tvshow;

import android.util.Log;

import com.sope.domain.websocket.SopeSocketMessage;

import rx.Subscriber;


public class TvListSubscription extends Subscriber<SopeSocketMessage> {
    private TvFragment fragment;

    public TvListSubscription(TvFragment fragment) {
        this.fragment = fragment;
    }

    public void onCompleted() {
        Log.d("General", "onCompleted()");
    }

    public void onError(Throwable e) {
        Log.e("General", "onError()", e);
    }

    public void onNext(SopeSocketMessage socketMessage) {
        if (socketMessage.messageType.isTvShowList()) {
            fragment.getTvShow().clear();
            fragment.getTvShow().addAll(socketMessage.categoryList);
            fragment.getActivity().runOnUiThread(() -> fragment.getTvTvShowDatamanager().notifyDataSetChanged());
        }

    }
}
