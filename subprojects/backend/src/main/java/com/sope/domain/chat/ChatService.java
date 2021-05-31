package com.sope.domain.chat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sope.SopeCache;
import com.sope.domain.EntityRepository;
import com.sope.domain.category.Category;
import com.sope.domain.category.CategoryNotFound;
import com.sope.domain.category.CategoryRepository;
import com.sope.domain.category.TvShowNotFound;
import com.sope.domain.firebase.MessageService;
import com.sope.domain.firebase.MessageToChat;
import com.sope.domain.sequence.SequenceService;

@Service
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private static final int MAX_HEADER_CHARS = 20;
    private static final int MAX_CHAT_NUMBERS_PER_QUERY = 100;

    private final ChatRepository chatRepository;
    private final EntityRepository entityRepository;
    private final SequenceService sequenceService;
    private final CategoryRepository categoryRepository;

    @Inject
    public ChatService(final ChatRepository chatRepository, final EntityRepository entityRepository, final MessageService messageService, final SequenceService sequenceService,
            final CategoryRepository generalRepository) {
        this.chatRepository = chatRepository;
        this.entityRepository = entityRepository;
        this.sequenceService = sequenceService;
        this.categoryRepository = generalRepository;
    }

    public Chat createTelevisionChat(final String header, final String showName) {
        final Optional<Category> tvShow = categoryRepository.getShowByName(showName);
        if (!tvShow.isPresent()) {
            throw new TvShowNotFound(showName);
        }

        final Chat chat = new Chat();
        chat.setCreated(new Date());
        chat.setHeader(header);
        chat.setCategory(tvShow.get());
        chat.setChatNumber(sequenceService.getNextChatNumber());
        chat.addUser();
        entityRepository.save(chat);

        return chat;

    }
    public Chat createGeneralChat(final String header, final String name) {
        final Optional<Category> general = categoryRepository.getGeneralByName(name);

        if (!general.isPresent()) {
            throw new CategoryNotFound(name);
        }

        final Chat chat = new Chat();
        chat.setCreated(new Date());
        chat.setHeader(header);
        chat.setCategory(general.get());
        chat.setChatNumber(sequenceService.getNextChatNumber());
        chat.addUser();
        entityRepository.save(chat);
        return chat;

    }
    public Chat createEventChat(final String header, final String name) {
        final Optional<Category> event = categoryRepository.getEventByName(name);

        if (!event.isPresent()) {
            throw new CategoryNotFound(name);
        }

        final Chat chat = new Chat();
        chat.setCreated(new Date());
        chat.setHeader(header);
        chat.setCategory(event.get());
        chat.setChatNumber(sequenceService.getNextChatNumber());
        chat.addUser();
        entityRepository.save(chat);
        return chat;

    }

    public Chat renameHeader(final long chatNumber, final String newHeader, final String username) {
        final Optional<Chat> persistedChat = chatRepository.getChatByChatNumber(chatNumber);
        if (persistedChat.isPresent()) {
            final Chat chat = persistedChat.get();
            LOGGER.info(String.format("Renamed chat header from '%s' to '%s' ", chat.getHeader(), newHeader));
            Optional.ofNullable(newHeader).ifPresent(header-> {
                chat.setHeader(newHeader.substring(0, Math.min(MAX_HEADER_CHARS, newHeader.length())));
                entityRepository.save(chat);

            });
            return chat;
        }

        throw new ChatTopicNotFound();
    }

    // FIXME yhdistä nämä
    public List<Chat> getTvShowChatsFor(final String name, final int currentPage) {
        final List<Chat> nextChat = chatRepository.getShowChatsByName(name, currentPage, MAX_CHAT_NUMBERS_PER_QUERY);
        if (nextChat.isEmpty()) {
            LOGGER.error("FIXME: No chat found. Creating new chat with header");
            return Lists.newArrayList(createTelevisionChat("Nimeä keskustelu", name));
        }
        return nextChat;
    }

    public List<Chat> getGeneralChatsFor(final String name, final int currentPage) {
        final List<Chat> nextChat = chatRepository.getGeneralChatsByName(name, currentPage, MAX_CHAT_NUMBERS_PER_QUERY);
        if (nextChat.isEmpty()) {
            LOGGER.error("FIXME: No general chat found. Creating new chat with header");
            return Lists.newArrayList(createGeneralChat("Nimeä keskustelu", name));
        }
        return nextChat;
    }
    public List<Chat> getEventChatsFor(final String name, final int currentPage) {
        final List<Chat> nextChat = chatRepository.getEventChatsByName(name, currentPage, MAX_CHAT_NUMBERS_PER_QUERY);
        if (nextChat.isEmpty()) {
            LOGGER.error("FIXME: No event chat found. Creating new chat with header");
            return Lists.newArrayList(createEventChat("Nimeä keskustelu", name));
        }
        return nextChat;
    }

    public void removeUserFromChat(final Long chatNumber) {
        final Optional<Chat> persistedChat = chatRepository.getChatByChatNumber(chatNumber);
        if (persistedChat.isPresent()) {
            synchronized (this) {
                final Chat chat = persistedChat.get();
                chat.removeUser();
                entityRepository.save(chat);
            }
        }

    }

    public void addUserToChat(final Long chatNumber) {
        final Optional<Chat> persistedChat = chatRepository.getChatByChatNumber(chatNumber);
        if (persistedChat.isPresent()) {
            // FIXME hienosäädä, jos tulee ongelmia.
            synchronized (this) {
                final Chat chat = persistedChat.get();
                chat.addUser();
                entityRepository.save(chat);
            }
        }

    }

    public void removeAllEmptyChatsWithLastMessageSent(final LocalDateTime lastMessageSent) {
        chatRepository.deleteChatsAgo(lastMessageSent);
    }

    public List<MessageToChat> getOldMessagesForChat(final Long chatNumber) {
        final List<MessageToChat> messages = SopeCache.getMessageForChat(chatNumber);
        if (messages.isEmpty()) {
            final List<ChatMessage> chatMessages = chatRepository.getMessagesFor(chatNumber, 40);
            for (final ChatMessage chatMessage : chatMessages) {
                SopeCache.putChatMessageToCache(new ChatMessageConverter().apply(chatMessage));
            }
        }
        return SopeCache.getMessageForChat(chatNumber);

    }

    public void saveMessage(final MessageToChat message) {
        final ChatMessage chatMessage = new ChatMessage();
        final Optional<Chat> chat = chatRepository.getChatByChatNumber(Long.valueOf(message.getChatNumber()));
        if (chat.isPresent()) {
            chatMessage.setChat(chat.get());
        }
        chatMessage.setUsername(message.getAuthor());
        chatMessage.setMessage(message.getMessage());
        chatMessage.setSendTime(message.getSendDate());
        entityRepository.save(chatMessage);

    }

}
