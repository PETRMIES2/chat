package com.sopeapp.tabs.general;

import android.content.Intent;
import android.util.Log;

import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.chat.ChatActivity;
import com.sopeapp.utilities.ChatPagination;
import com.sopeapp.websocket.WebSocketMessageManager;

import rx.Subscriber;

/**
 * Created by petra on 7.5.2017.
 */

public class GeneralChatSubscription extends Subscriber<SopeSocketMessage> {
    private GeneralFragment fragment;

    public GeneralChatSubscription(GeneralFragment fragment) {
        this.fragment = fragment;
    }

    public void onCompleted() {
        Log.d("General", "onCompleted()");
    }

    public void onError(Throwable e) {
        Log.e("General", "onError()", e);
    }

    public void onNext(SopeSocketMessage socketMessage) {
        if (socketMessage.messageType.isGetGeneralChats() && ChatPagination.hasChats(socketMessage.chatList)) {
            WebSocketMessageManager.joinChat(ChatPagination.getFirstChat(socketMessage.chatList), socketMessage.category);

            SharedPreferencesManager.updateSocketMessage(fragment.getActivity().getApplicationContext(), socketMessage);
            Intent moveToChatViewIntent = new Intent(fragment.getActivity(), ChatActivity.class);

            fragment.startActivityForResult(moveToChatViewIntent, 600);
            fragment.getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

        }

    }
}
