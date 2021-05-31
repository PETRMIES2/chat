package com.sope.domain.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.sope.domain.EntityRepository;
import com.sope.domain.category.CategoryType;

@Service
public class ChatRepository {
    private final EntityRepository entityRepository;

    @Inject
    public ChatRepository(final EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public Optional<Chat> getChatByChatNumber(final long chatNumber) {
        final String hql = "from Chat where chatNumber = :chatNumber";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>().put("chatNumber", chatNumber).build();

        return entityRepository.getUnique(hql, parameters);
    }

    public List<Chat> getGeneralChatsByName(final String name, final int currentPage, final int chatsPerPage) {
        return getPageChatsByCategoryName(name, CategoryType.GENERAL, currentPage, chatsPerPage);
    }
    public List<Chat> getShowChatsByName(final String name, final int currentPage, final int chatsPerPage) {
        return getPageChatsByCategoryName(name, CategoryType.SHOW, currentPage, chatsPerPage);
    }
    public List<Chat> getEventChatsByName(final String name, final int currentPage, final int chatsPerPage) {
        return getPageChatsByCategoryName(name, CategoryType.EVENT, currentPage, chatsPerPage);
    }

    public void deleteChatsAgo(final LocalDateTime lastMessageSent) {
        final String hql = "delete from Chat where lastMessageSent <= :lastMessageSent or (lastMessageSent is null and created <= :lastMessageSent)";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("lastMessageSent", lastMessageSent)
                .put("manyHoursInThePast", lastMessageSent.minusHours(6))
                .build();

        entityRepository.delete(hql, parameters);

    }

    public List<ChatMessage> getMessagesFor(final Long chatNumber, final int limit) {
        final String hql = "select chatMessage from ChatMessage chatMessage left join fetch chatMessage.chat chat where chat.chatNumber = :chatNumber order by chatMessage.sendTime";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("chatNumber", chatNumber)
                .build();

        return entityRepository.get(hql, parameters, 0, limit);
    }

    private List<Chat> getPageChatsByCategoryName(final String name, final CategoryType type, final int currentPage, final int chatsPerPage) {
        final String hql = "from Chat as c left join fetch c.category category where category.type = :type and category.name = :name order by c.userCount desc, c.header desc";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("name", name)
                .put("type", type)
                .build();

        return entityRepository.get(hql, parameters, currentPage, chatsPerPage);
    }
}
