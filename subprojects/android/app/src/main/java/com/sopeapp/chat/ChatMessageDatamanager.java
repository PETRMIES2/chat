package com.sopeapp.chat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sope.domain.firebase.MessageToChat;
import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;

import java.util.List;

public class ChatMessageDatamanager extends RecyclerView.Adapter<ChatMessageViewHolder> {
    private String username;
    private MessageLayoutFragment parentLayoutFragment;


    public ChatMessageDatamanager(String username, MessageLayoutFragment chatPage) {
        this.username = username;
        this.parentLayoutFragment = chatPage;
    }


    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.new_message_layout;
        View chatMessageObject = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ChatMessageViewHolder(chatMessageObject);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder viewHolder, int position) {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());
        ChatDTO chatPage = sopeMessage.chatList.getChats().get(getChatPage());
        List<MessageToChat> messageList = ChatMessageContainer.getAllMessagesToChat(chatPage.chatNumber);
        if (!messageList.isEmpty()) {
            final MessageToChat message = messageList.get(position);
            if (viewHolder.author != null) {
                viewHolder.author.setText(message.getAuthor());
                    viewHolder.image.findViewById(viewHolder.image.getId()).setBackgroundColor(stringToColor(message.getAuthor()));
            }
            viewHolder.message.setText(message.getMessage());
            viewHolder.sendTime.setText(message.getFormattedDate());
        }
    }


    public int stringToColor(String string) {
        int i = string.hashCode();
        return Color.rgb(((i >> 16) & 0xFF), ((i >> 8) & 0xFF),
                (i & 0xFF));
    }


    @Override
    public int getItemViewType(int position) {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());
        ChatDTO chatPage = sopeMessage.chatList.getChats().get(getChatPage());
        List<MessageToChat> messageList = ChatMessageContainer.getAllMessagesToChat(chatPage.chatNumber);
        if (!messageList.isEmpty()) {
            final MessageToChat message = messageList.get(position);
            if (message != null && message.getAuthor().equals(username)) {
                return ChatLayoutType.SELF.getViewValue();
            }

            return ChatLayoutType.OTHER.getViewValue();

        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());
        ChatDTO chatPage = sopeMessage.chatList.getChats().get(getChatPage());
        return ChatMessageContainer.getAllMessagesToChat(chatPage.chatNumber).size();
    }

    public int getChatPage() {
        return parentLayoutFragment.getArguments().getInt(MessageLayoutFragment.CHAT_PAGE);
    }
}
