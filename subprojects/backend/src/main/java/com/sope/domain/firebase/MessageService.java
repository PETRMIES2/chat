package com.sope.domain.firebase;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sope.domain.EntityRepository;
import com.sope.domain.ResourceService;
import com.sope.domain.chat.Chat;
import com.sope.domain.chat.ChatRepository;
import com.sope.domain.user.User;
import com.sope.domain.user.UserRepository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// FIXME RXJAVA jos tarvitaan observable-härpäkkeitä

// https://firebase.google.com/docs/cloud-messaging/topic-messaging
@Service

public class MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final EntityRepository entityRepository;

    @Inject
    public MessageService(final UserRepository userRepository, final ChatRepository chatRepository,
            final EntityRepository entityRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.entityRepository = entityRepository;
    }

    private final OkHttpClient.Builder client;

    {
        client = new OkHttpClient.Builder();
        client.authenticator((route, response) -> response.request().newBuilder()
                .header("Authorization", "key=" + ResourceService.FIREBASE_AUTHORIZATION_KEY).build());
        client.connectTimeout(10, TimeUnit.SECONDS);
        client.writeTimeout(10, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
    }

    public void sendMessageToChat(final MessageToChat message) {
        final MessageBase messageBase = new MessageBase(message).toChannel();
        //        updateLastMessageSent(message.getChatNumber());
        post(ResourceService.FIREBASE_TARGET_URL, new Gson().toJson(messageBase));
    }

    public void sendMessageToUser(final String targetUsername, final MessageToUser message) {
        final Optional<User> persistedUser = userRepository.findByUsername(targetUsername);
        if (!persistedUser.isPresent() || persistedUser.get().hasInvalidToken()) {
            // throw error
            LOGGER.info(String.format("Cannot find user %s", targetUsername));
            return;
        }
        message.setTargetUserFirebaseToken(persistedUser.get().getFirebaseToken());
        post(ResourceService.FIREBASE_TARGET_URL, new Gson().toJson(new MessageBase(message).toChannel()));
    }

    private void post(final String url, final String json) {
        LOGGER.info(json);
        try {
            System.err.println(ResourceService.FIREBASE_AUTHORIZATION_KEY);
            final RequestBody body = RequestBody.create(JSON, json);
            final Request request = new Request.Builder().url(url).post(body).build();
            final Response response = client.build().newCall(request).execute();
            System.err.println(response);
            response.close();
            LOGGER.info("POST RESPONSE ", response);
        } catch (final Exception e) {
            LOGGER.error("POSTING MESSAGE ERROR", e.getMessage());
        }
    }

    public void updateLastMessageSent(final Long chatNumber) {
        if (chatNumber != null) {
            final Optional<Chat> persistedChat = chatRepository.getChatByChatNumber(chatNumber);
            if (persistedChat.isPresent()) {
                final Chat chat = persistedChat.get();
                chat.setLastMessageSent(new Date());
                entityRepository.save(chat);
            }
        }
    }

    public static void main(final String[] args) {
        // Huom. Jos ajat tätä niin huomio, että firebase blokkaa seuraavat pyynnöt ja UI ei toimi oikein. Liittyy jotenkin autentikointiin.
        final MessageService channelTest = new MessageService(null, null, null);
        final MessageToChat test = new MessageToChat("Bäkkäriltä viesti, \ue32d  jonka pitäisi näkyäa. Ylipitkä ja toimii varmasti oikein", "Jeesus", new Date(), 4L,
                "Otsikko" + Math.random());
        // channelTest.sendMessageToChat(test);
        // MessageToUser toUser = new MessageToUser("Direct message to user",
        // "Serveri lähettää viestin",
        // "eoM7qOGJqmQ:APA91bH8CKWF3C5V6YMIM0JsuEeN2RDanaXO6z6EITpT6dbqsD-izySzY7xhUaSd8gyRXMhQ3eZs92BGSdYCyG7ZLaq5kXxNmVh8vjoD3A-1cwoVLadofwspxt2UUGEAUDvjtzzbeWIJ");
        // channelTest.post(ResourceService.FIREBASE_TARGET_URL, new
        // Gson().toJson(new MessageBase(toUser).toUser()));
        channelTest.post(ResourceService.FIREBASE_TARGET_URL, new Gson().toJson(new MessageBase(test).toChannel()));

        // eoM7qOGJqmQ:APA91bH8CKWF3C5V6YMIM0JsuEeN2RDanaXO6z6EITpT6dbqsD-izySzY7xhUaSd8gyRXMhQ3eZs92BGSdYCyG7ZLaq5kXxNmVh8vjoD3A-1cwoVLadofwspxt2UUGEAUDvjtzzbeWIJ
    }
}
