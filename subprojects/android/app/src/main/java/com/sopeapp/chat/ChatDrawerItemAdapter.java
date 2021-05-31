package com.sopeapp.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopeapp.R;

import java.util.List;

public class ChatDrawerItemAdapter extends ArrayAdapter<ChatDrawerItem> {

    Context mContext;
    int layoutResourceId;
    List<ChatDrawerItem> userList = null;

    public ChatDrawerItemAdapter(Context context, int layoutResourceId, List<ChatDrawerItem> data) {

        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.userList = data;
    }

    @Override
    public View getView(int position, View listItem, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView image = (ImageView) listItem.findViewById(R.id.chatUserIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.user_on_channel);

        ChatDrawerItem drawerItem = userList.get(position);
        textViewName.setText(drawerItem.name);

        image.setImageResource(drawerItem.icon);

        return listItem;
    }
}