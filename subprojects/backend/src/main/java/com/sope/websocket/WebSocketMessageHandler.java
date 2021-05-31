package com.sope.websocket;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.sope.SopeCache;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.category.Category;
import com.sope.domain.category.CategoryRepository;
import com.sope.domain.category.CategoryType;
import com.sope.domain.chat.Chat;
import com.sope.domain.chat.ChatService;
import com.sope.domain.firebase.MessageService;
import com.sope.domain.firebase.MessageToChat;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.ChatListDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sope.domain.websocket.WebSocketMessageType;
import com.sope.websocket.command.ObservableService;

import io.reactivex.disposables.Disposable;

// FIXME refactor this class after everything is moved to here
public class WebSocketMessageHandler extends AbstractWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    @Inject
    private Gson gson;
    @Inject
    private WebSocketSessionHandler sessionHandler;
    @Inject
    private ObservableService observableService;

    @Inject
    private SopeTransactionExecutor transactionExecutor;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private ChatService chatService;
    @Inject
    private MessageService messageService;

    private static final String HEADER_RENAME_MESSAGE = "'%s' nimesi keskustelun: %s";

    public WebSocketMessageHandler() {
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
        System.out.println(LocalDateTime.now() + " New websocket connection was established " + webSocketSession.getId());
        System.out.println("disposable " + sessionHandler);
        LOGGER.info("New websocket connection was established " + webSocketSession.getId());
        sessionHandler.register(webSocketSession);

        final Disposable tabs = observableService.getGlobalObservable().subscribe(message -> {
            sendMessageToClient(webSocketSession, message);
        });
        sessionHandler.registerDisposables(webSocketSession, DisposableWrapper.buildGeneral(tabs, "ANDROID_GLOBAL"));
        super.afterConnectionEstablished(webSocketSession);
    }


    @Override
    public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage webSocketMessage) {
        try {

            final SopeSocketMessage socketMessage = gson.fromJson(webSocketMessage.getPayload(), SopeSocketMessage.class);
            // To functional strategy or ...
            System.out.println(socketMessage.messageType);
            if (socketMessage.messageType.isJoinChat()) {
                joinChat(webSocketSession, socketMessage);
            }
            if (socketMessage.messageType.isRemoveUserFromChat()) {
                removeFromChat(webSocketSession, socketMessage);
            }
            if (socketMessage.messageType.isGetOldMessages()) {
                getOldMessages(socketMessage);
            }
            if (socketMessage.messageType.isGetGeneralChats()) {
                getChats(WebSocketMessageType.GET_GENERAL_CHATS, () -> chatService.getGeneralChatsFor(socketMessage.category.getName(), 0),
                        general -> CategoryConverter.build(general, CategoryType.GENERAL));
            }
            if (socketMessage.messageType.isGetEventChats()) {
                getChats(WebSocketMessageType.GET_EVENTS_CHATS, () -> chatService.getEventChatsFor(socketMessage.category.getName(), 0), event -> CategoryConverter.build(event, CategoryType.EVENT));
            }
            if (socketMessage.messageType.isGetTvShowChats()) {
                getChats(WebSocketMessageType.GET_TVSHOW_CHATS, () -> chatService.getTvShowChatsFor(socketMessage.category.getName(), 0), show -> CategoryConverter.build(show, CategoryType.SHOW));
            }
            if (socketMessage.messageType.isRenameChat()) {
                renameChat(socketMessage);

            }
            if (socketMessage.messageType.isCreateChat()) {
                removeFromChat(webSocketSession, socketMessage);
                createChat(webSocketSession, socketMessage);
            }
            if (socketMessage.messageType.isNextChat()) {

            }
            // Lähetä GENERAL, OHJELMAT JA EVENTS
            // Nämä ulos, koska tiedot ovat websocketsession-handlerissä
            // Thread.pool, joka zekkaan viimeisen ja nykyhetken tilanteen
            // cacheen ja lähettää, jos jotain on muuttunut. Kerran kahdessa
            // sekunnissa. Meiluiten push-arkkitehtuuri
            if (socketMessage.messageType.isEventList()) {
                sendCategoryList(webSocketSession, WebSocketMessageType.EVENTS_LIST, CategoryType.EVENT, () -> categoryRepository.getEvents());
            }
            if (socketMessage.messageType.isGeneralList()) {
                sendCategoryList(webSocketSession, WebSocketMessageType.GENERAL_LIST, CategoryType.GENERAL, () -> categoryRepository.getGeneral());
            }
            if (socketMessage.messageType.isTvShowList()) {
                sendCategoryList(webSocketSession, WebSocketMessageType.TVSHOWS_LIST, CategoryType.SHOW, () -> categoryRepository.getShows());
            }
            if (socketMessage.messageType.isPopularList()) {
                final SopeSocketMessage popularList = new SopeSocketMessage(WebSocketMessageType.POPULAR_LIST);
                popularList.categoryList = observableService.getCategoriesByPopularity();
                sendMessageToClient(webSocketSession, popularList);
            }

        } catch (final Exception e) {
            e.printStackTrace();
            LOGGER.info("Cannot handle received socket message", e);
        }
    }

    private void sendCategoryList(final WebSocketSession webSocketSession, final WebSocketMessageType messageType, final CategoryType categoryType, final Supplier<List<Category>> categoryFetcher) {
        transactionExecutor.read(() -> {
            try {
                final SopeSocketMessage categoryList = new SopeSocketMessage(messageType);
                categoryList.categoryList = categoryFetcher.get().stream().map(general -> CategoryConverter.build(general, categoryType)).collect(Collectors.toList());
                sendMessageToClient(webSocketSession, categoryList);
            } catch (final IOException e) {
                e.printStackTrace();
                LOGGER.info(String.format("Cannot get Categories for %s,%s", messageType, categoryType), e);
            }
        });
    }

    private final Map<CategoryType, VarArgFunction<Chat, String>> chatCreator = new ImmutableMap.Builder<CategoryType, VarArgFunction<Chat, String>>()
            .put(CategoryType.EVENT, container -> chatService.createEventChat(container[0], container[1]))
            .put(CategoryType.GENERAL, container -> chatService.createGeneralChat(container[0], container[1]))
            .put(CategoryType.SHOW, container -> chatService.createTelevisionChat(container[0], container[1])).build();

    private void createChat(final WebSocketSession webSocketSession, final SopeSocketMessage socketMessage) {
        transactionExecutor.write(() -> {
            final VarArgFunction<Chat, String> createChat = chatCreator.get(socketMessage.category.getCategoryType());
            final Chat chat = createChat.apply(socketMessage.chat.chatHeader, socketMessage.category.getName());

            final ChatDTO createdChat = new ChatDTO(chat.getChatNumber(), chat.getHeader());

            final Disposable chatDisposable = observableService.subscribeChat(createdChat.chatNumber).subscribe(message -> {
                System.out.println("*********CREATE Sending message " + message.messageType);
                sendMessageToClient(webSocketSession, message);
            });
            sessionHandler.registerDisposables(webSocketSession, DisposableWrapper.buildChat(chatDisposable, "Chat number " + chat.getChatNumber()));

            observableService.getChatSubscription(createdChat.chatNumber).ifPresent(subscription -> {
                final SopeSocketMessage chatCreated = new SopeSocketMessage(WebSocketMessageType.CHAT_CREATED);
                chatCreated.chat = createdChat;
                chatCreated.category = socketMessage.category;
                subscription.onNext(chatCreated);
            });

        });
    }

    private void renameChat(final SopeSocketMessage socketMessage) {
        transactionExecutor.write(() -> {
            final Chat chat = chatService.renameHeader(socketMessage.chat.chatNumber, socketMessage.chat.chatHeader, socketMessage.username);

            // siirrä muualle. Testiä varten tässä
            final MessageToChat headerChangedUpdate = new MessageToChat(String.format(HEADER_RENAME_MESSAGE, socketMessage.username, chat.getHeader()), SopeCache.SYSTEM_USER, new Date(),
                    chat.getChatNumber(), chat.getHeader());
            messageService.sendMessageToChat(headerChangedUpdate);
        });
    }

    private <T> void getChats(final WebSocketMessageType type, final Supplier<List<Chat>> chatFetcher, final Function<Category, CategoryDTO> converter) {
        transactionExecutor.write(() -> {
            final Function<Chat, ChatDTO> mapper = chat -> new ChatDTO(chat.getChatNumber(), chat.getHeader());
            final SopeSocketMessage chatsMessages = new SopeSocketMessage(type);
            final List<Chat> persistedChats = chatFetcher.get();
            final List<ChatDTO> chats = persistedChats.stream().map(mapper).collect(Collectors.toList());

            chatsMessages.chatList = new ChatListDTO(chats);
            persistedChats.stream().findFirst().map(Chat::getCategory).ifPresent(category -> {
                chatsMessages.category = converter.apply(category);
            });

            observableService.getGlobalObservable().onNext(chatsMessages);
        });

    }

    private void getOldMessages(final SopeSocketMessage socketMessage) {
        transactionExecutor.read(() -> {
            final ChatDTO chat = socketMessage.chat;
            observableService.getChatSubscriptionForOldMessages(chat.chatNumber).ifPresent(subscription -> {
                final SopeSocketMessage messages = new SopeSocketMessage(WebSocketMessageType.GET_OLD_MESSAGES);
                messages.oldMessages = chatService.getOldMessagesForChat(chat.chatNumber);
                subscription.onNext(messages);
            });
        });
    }

    private void removeFromChat(final WebSocketSession webSocketSession, final SopeSocketMessage socketMessage) {
        final ChatDTO chat = socketMessage.chat;

        observableService.getChatSubscription(chat.chatNumber).ifPresent(subscription -> {
            final SopeSocketMessage userLeftChat = new SopeSocketMessage(WebSocketMessageType.USER_LEFT_CHAT);
            userLeftChat.messageToChat = socketMessage.messageToChat;
            subscription.onNext(userLeftChat);
        });
        observableService.updatePopular(chat.chatNumber, socketMessage.category, observableService.decreaseOneFromUserInPopular());
        sessionHandler.clean(webSocketSession);
        SopeCache.removeUserFromCache(socketMessage.username, chat.chatNumber);

    }

    private void joinChat(final WebSocketSession webSocketSession, final SopeSocketMessage socketMessage) {
        sessionHandler.clean(webSocketSession);
        final ChatDTO chat = socketMessage.chat;
        final Disposable chatDisposable = observableService.subscribeChat(chat.chatNumber).subscribe(message -> {
            System.out.println("********* Join Chat: Sending message " + message.messageType);
            sendMessageToClient(webSocketSession, message);
        });
        sessionHandler.registerDisposables(webSocketSession, DisposableWrapper.buildChat(chatDisposable, "Chat number " + chat.chatNumber));

        observableService.getChatSubscription(chat.chatNumber).ifPresent(subscription -> {
            System.out.println("********* Sending join to chat " + subscription);
            final SopeSocketMessage userJoinedChat = new SopeSocketMessage(WebSocketMessageType.USER_JOINED_CHAT);
            userJoinedChat.messageToChat = socketMessage.messageToChat;
            subscription.onNext(userJoinedChat);

            System.out.println("********* Sending user list update " + subscription);
            SopeCache.updateChatUserCache(socketMessage.username, chat.chatNumber);

            final SopeSocketMessage userListUpdate = new SopeSocketMessage(WebSocketMessageType.CHAT_USERLIST_UPDATE);
            userListUpdate.usersInChat = SopeCache.getChatUsers(chat.chatNumber);
            subscription.onNext(userListUpdate);
        });
        observableService.updatePopular(chat.chatNumber, socketMessage.category, observableService.addOneToUserInPopular());

    }


    private void sendMessageToClient(final WebSocketSession webSocketSession, final Object message) throws IOException {
        System.out.println(gson.toJson(message));
        final TextMessage socketMessage = new TextMessage(gson.toJson(message));
        webSocketSession.sendMessage(socketMessage);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus status) throws Exception {
        System.out.println(LocalDateTime.now() + " Connection was closed");
        LOGGER.info("Connection was closed " + webSocketSession.getId());
        sessionHandler.disposeSubscriptions(webSocketSession);
        super.afterConnectionClosed(webSocketSession, status);
    }

}
