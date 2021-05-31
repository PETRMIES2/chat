package com.sopeapp.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sopeapp.R;
import com.sope.domain.firebase.Message;
import com.sope.domain.firebase.MessageToChat;
import com.sope.domain.firebase.MessageToUser;
import com.sope.domain.firebase.MessageType;
import com.sopeapp.SopeApplication;
import com.sopeapp.chat.ChatActivity;

import java.util.Map;

public class SopeFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SopeFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> receivedMessage = remoteMessage.getData();
        for (Map.Entry<String, String> entry : receivedMessage.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());

        }
        String messageType = receivedMessage.get(Message.MESSAGETYPE_TYPE);
        Log.d(TAG, messageType);
        if (MessageType.get(messageType) == MessageType.TOPIC) {
            Log.d(TAG, "TOPIC-message");
            MessageToChat messageToChat = new MessageToChat(receivedMessage);
            ((SopeApplication) getApplication()).messageSubscription().onNext(messageToChat);

        } else {
            Log.d(TAG, "Direct user message");
            MessageToUser messageToUser = new MessageToUser(receivedMessage);
            sendNotificationFromUser(messageToUser);
        }
        Log.d(TAG, "From: " + remoteMessage.getFrom());
    }


    private void sendNotificationFromUser(MessageToUser message) {
        // FIXME viestin klikkaus kaataa ohjelman.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sope_icon)
                        .setContentTitle(getString(R.string.message_from_user, message.getMessageFrom()))
                        .setContentText(message.getMessage());
        Intent resultIntent = new Intent(this, ChatActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(102, mBuilder.build());
    }


}