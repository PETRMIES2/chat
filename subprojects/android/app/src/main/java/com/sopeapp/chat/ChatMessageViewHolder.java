package com.sopeapp.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sopeapp.R;

import org.w3c.dom.Text;


public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView message;
    public TextView author;
    public TextView sendTime;
    public TextView image;



    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        message = (TextView) itemView.findViewById(R.id.chat_message);
        author = (TextView) itemView.findViewById(R.id.chat_username);
        sendTime = (TextView) itemView.findViewById(R.id.chat_timeText);
        image = (TextView) itemView.findViewById(R.id.new_message_user_icon);
    }

}
