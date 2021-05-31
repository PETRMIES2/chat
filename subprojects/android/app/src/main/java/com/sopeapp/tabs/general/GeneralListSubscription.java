package com.sopeapp.tabs.general;

import android.util.Log;

import com.sope.domain.websocket.SopeSocketMessage;

import rx.Subscriber;


public class GeneralListSubscription extends Subscriber<SopeSocketMessage> {
    private GeneralFragment fragment;

    public GeneralListSubscription(GeneralFragment fragment) {
        this.fragment = fragment;
    }

    public void onCompleted() {
        Log.d("General", "onCompleted()");
    }

    public void onError(Throwable e) {
        Log.e("General", "onError()", e);
    }

    public void onNext(SopeSocketMessage socketMessage) {
        if (socketMessage.messageType.isGeneralList()) {
            fragment.getGeneralCategories().clear();
            fragment.getGeneralCategories().addAll(socketMessage.categoryList);
            fragment.getActivity().runOnUiThread(() -> fragment.getGeneralDatamanager().notifyDataSetChanged());
        }


    }
}
