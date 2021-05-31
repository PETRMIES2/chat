package com.sope.websocket;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.sope.domain.chat.ChatService;
import com.sope.websocket.command.ObservableService;

@Service
public class WebSocketSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSessionHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedDeque<DisposableWrapper>> socketSubscriptions = new ConcurrentHashMap<>();

    private final ObservableService observableService;
    // This is only for testin because something initializes this service twice
    private final String debuggingSocketSessionIndentification;

    @Inject
    public WebSocketSessionHandler(final ObservableService observableService, final ChatService chatService) {
        this.observableService = observableService;
        debuggingSocketSessionIndentification = String.valueOf(Math.random());

        scheduler.scheduleWithFixedDelay(() -> {
            sessionMap.keySet().forEach(k -> {
                try {
                    final WebSocketSession socket = sessionMap.get(k);
                    if (!socket.isOpen()) {
                        disposeSubscriptions(socket);
                        clean(socket);
                        socket.close();
                        sessionMap.remove(k);
                        System.out.println(LocalDateTime.now() + " Socket was closed. Removing socket id" + k);
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    LOGGER.error("Error while closing websocket session: {}", e);
                }
            });
            System.out.println("\n\nid " + debuggingSocketSessionIndentification + " " + new Date() + ": " + this);
        }, 10, 10, TimeUnit.SECONDS);

        scheduler.scheduleWithFixedDelay(() -> {
            final LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
            chatService.removeAllEmptyChatsWithLastMessageSent(oneDayAgo);
            System.out.println("Cleaning old Chats " + new Date() + ": " + this);
        }, 10, 10, TimeUnit.MINUTES);

    }

    public void register(final WebSocketSession session) {
        sessionMap.put(session.getId(), session);
    }

    public void registerDisposables(final WebSocketSession session, final DisposableWrapper subscription) {
        ConcurrentLinkedDeque<DisposableWrapper> disposables;
        if (socketSubscriptions.containsKey(session.getId())) {
            disposables = socketSubscriptions.get(session.getId());
        } else {
            disposables = new ConcurrentLinkedDeque<>();
            socketSubscriptions.put(session.getId(), disposables);
        }
        disposables.add(subscription);
    }

    public void disposeSubscriptions(final WebSocketSession session) {
        if (socketSubscriptions.containsKey(session.getId())) {
            socketSubscriptions.get(session.getId()).stream().forEach(wrapper -> {
                System.out.println("disposing observer for socket " + session.getId() + " identification " + wrapper.identification);
                LOGGER.info("disposing observer for socket " + session.getId() + " identification " + wrapper.identification);
                wrapper.subscription.dispose();
            });
            socketSubscriptions.remove(session.getId());
        }
    }

    @Override
    public String toString() {
        return String.join(" ",
                "\n socket-disposable ",
                socketSubscriptions.toString(),
                "\n session ",
                sessionMap.toString(),
                "\n chat ",
                observableService.toString()
                );
    }

    public void clean(final WebSocketSession session) {
        if (socketSubscriptions.containsKey(session.getId())) {
            final ConcurrentLinkedDeque<DisposableWrapper> disposables = socketSubscriptions.get(session.getId());
            disposables.stream().filter(p->p.type == DisposableType.CHAT).forEach(wrapper -> {
                System.out.println("********* disposing chat for socket " + session.getId() + " identification " + wrapper.identification);
                LOGGER.info("disposing chat for socket " + session.getId() + " identification " + wrapper.identification);
                wrapper.subscription.dispose();
                disposables.remove(wrapper);
            });
        }
        observableService.cleanChatObservables();

    }

}
