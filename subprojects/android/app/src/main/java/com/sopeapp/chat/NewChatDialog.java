package com.sopeapp.chat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.sopeapp.websocket.WebSocketMessageManager;
import com.sopeapp.R;

public class NewChatDialog extends DialogFragment {
    MessageLayoutFragment messageLayoutFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.chat_new, null))
                .setPositiveButton(R.string.create_new_chat_button, getNewChatDialog())
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    Log.i("Create new cancelled", "Negative click!");
                    dialog.cancel();
                });
        return builder.create();
    }

    public DialogInterface.OnClickListener getNewChatDialog() {
        return (dialog, id) -> {
            EditText newChatHeader = (EditText) getDialog().findViewById(R.id.chat_new);

            String newChatName = newChatHeader.getText().toString();
            if (!TextUtils.isEmpty(newChatName)) {
                WebSocketMessageManager.createChat(newChatName);
            }

        };
    }

    public void setTargetFragment(MessageLayoutFragment fragment) {
        messageLayoutFragment = fragment;
    }

}
