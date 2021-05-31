package com.sopeapp.chat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.websocket.WebSocketMessageManager;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.utilities.ChatPagination;

public class ChatRenameHeaderDialog extends DialogFragment {

    MessageLayoutFragment messageLayoutFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.chat_rename_header, null))
                // Add action buttons
                .setPositiveButton(R.string.chat_rename, (dialog, id) -> {
                    ChatActivity chatActivity = (ChatActivity) getActivity();
                    EditText changeHeaderEdit = (EditText) getDialog().findViewById(R.id.chat_rename_header);
                    String newHeader = changeHeaderEdit.getText().toString();
                    if (!TextUtils.isEmpty(newHeader)) {
                        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(chatActivity.getApplicationContext());
                        ChatDTO currentChat = ChatPagination.getCurrentChat(sopeMessage.chatList);
                        WebSocketMessageManager.renameChatHeader(newHeader, currentChat.chatNumber);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    Log.i("ChangeHeaderCancel", "Negative click!");
                    dialog.cancel();
                });
        return builder.create();
    }

    public void setTargetFragment(MessageLayoutFragment fragment) {
        messageLayoutFragment = fragment;
    }
}
