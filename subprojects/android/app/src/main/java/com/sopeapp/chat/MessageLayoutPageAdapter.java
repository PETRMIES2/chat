package com.sopeapp.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by petra on 15.10.2016.
 */

public class MessageLayoutPageAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, MessageLayoutFragment> chatPages = new HashMap<Integer,MessageLayoutFragment>();
    private FragmentManager fragmentManager;
    public MessageLayoutPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getItemPosition(Object object) {
        Fragment fragment = (Fragment) object;

//        for(int i = 0; i < getCount(); i++) {
//
//            Fragment item = getItem(i);
//            if(item.equals(fragment)) {
//                // Tässä on failure. Aiheuttaa kaatumisen;:  java.lang.IllegalStateException: Failure saving state: active MessageLayoutFragment{89a1b65} has cleared index: -1
//                final FragmentTransaction ft = fragmentManager.beginTransaction();
//                ft.detach(fragment);
//                ft.attach(item);
//                ft.commit();
//                return i;
//            }
//        }

        for(Map.Entry<Integer, MessageLayoutFragment> entry : chatPages.entrySet()) {
            if(entry.getValue().equals(fragment)) {
                chatPages.remove(entry.getKey());
                break;
            }
        }

        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int i) {
        if (chatPages.containsKey(i)) {
            return chatPages.get(i);
        }
        MessageLayoutFragment fragment = new MessageLayoutFragment();
        Bundle args = new Bundle();
        args.putInt(MessageLayoutFragment.CHAT_PAGE, i);
        fragment.setArguments(args);

        chatPages.put(i, fragment);

        return fragment;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MessageLayoutFragment fragment = (MessageLayoutFragment) super.instantiateItem(container, position);
        chatPages.put(position, fragment);
        return fragment;
    }
    @Override
    public int getCount() {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());
        return sopeMessage.chatList.getChats().size();
    }

//    public void refreshFragment(int chatPageNumber) {
//        MessageLayoutFragment messageLayoutFragment = (MessageLayoutFragment)chatPages.get(chatPageNumber);
//        if (messageLayoutFragment != null) {
//            // TODO Tämä aiheuttaa erilaisia ongelmia refreshin kanssa. (+ ChatActivity.subsciber-observable)
////            notifyDataSetChanged();
//            messageLayoutFragment.updateFramentData();
//            //fragmentManager.beginTransaction().detach(fragment).attach(fragment).commit();
//        }
//    }

    public void destroyItem (ViewGroup container, int position, Object object) {
        chatPages.remove(position);
        super.destroyItem(container, position, object);
    }

    public MessageLayoutFragment getFragment(int page) {
        return chatPages.get(page);
    }

}
