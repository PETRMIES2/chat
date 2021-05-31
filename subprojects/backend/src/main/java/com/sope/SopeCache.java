package com.sope;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.userdetails.UserDetails;

import com.google.api.client.util.Lists;
import com.google.common.collect.EvictingQueue;
import com.sope.domain.firebase.MessageToChat;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class SopeCache {
    public static final int MAX_OLD_MESSAGES_FOR_CHAT = 40;
    private static final long THREE_MINUTES = TimeUnit.MINUTES.toSeconds(3);
    private static final long TEN_MINUTES = TimeUnit.MINUTES.toSeconds(10);
    private static final long HOUR = TimeUnit.HOURS.toSeconds(1);
    private static final CacheManager sopeCache = CacheManager.create();
    private static Cache tokenCache;
    private static Cache usernameCache;
    private static Cache chatMessageCache;
    public static Cache chatUserCache;
    public static final String SYSTEM_USER = "SOPE";
    private static final String CHAT_MESSAGES = "%s_MESSAGES";

    static {
        tokenCache = new Cache(createDefaultCache("tokenCache", HOUR));
        usernameCache = new Cache(createDefaultCache("usernameCache", THREE_MINUTES));
        chatMessageCache = new Cache(createDefaultCache("chatMessageCache", HOUR * 3));

        final CacheConfiguration chatUserCacheConfiguration = createDefaultCache("chatUserCache", TEN_MINUTES);
        final Searchable searchable = new Searchable();
        chatUserCacheConfiguration.addSearchable(searchable);
        chatUserCache = new Cache(chatUserCacheConfiguration);

        sopeCache.addCache(tokenCache);
        sopeCache.addCache(usernameCache);
        sopeCache.addCache(chatMessageCache);
        sopeCache.addCache(chatUserCache);
    }

    private static CacheConfiguration createDefaultCache(final String cacheName, final long timeToIdleSeconds) {
        final CacheConfiguration configuration = new CacheConfiguration(cacheName, 500000);
        configuration.eternal(false);
        configuration.setTimeToIdleSeconds(timeToIdleSeconds);
        configuration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
        return configuration;
    }

    public static void putUserDetailsToCache(final String username, final UserDetails userDetails) {
        tokenCache.put(new Element(username, userDetails));
    }

    public static UserDetails getCachedUserDetails(final String username) {
        return (UserDetails) tokenCache.get(username).getObjectValue();
    }

    public static boolean stillValidInCache(final String username) {
        // username unique
        final Element cacheElement = tokenCache.get(username);
        if (cacheElement != null) {
            return true;
        }
        return false;
    }

    public static void removeUserFromCache(final String username) {
        tokenCache.remove(username);
    }

    public static boolean getCachedUserName(final String username) {
        final Element cacheElement = usernameCache.get(username);
        if (cacheElement != null) {
            return true;
        }
        return false;
    }

    public static void reserverUsername(final String username) {
        usernameCache.put(new Element(username, null));
    }

    public static void putChatMessageToCache(final MessageToChat message) {
        final String cacheKey = String.format(CHAT_MESSAGES, message.getChatNumber());
        EvictingQueue<MessageToChat> messages;
        Element cachedMessages = chatMessageCache.get(cacheKey);
        if (cachedMessages != null) {
            messages = ((EvictingQueue<MessageToChat>) cachedMessages.getObjectValue());
            messages.add(message);
            chatMessageCache.replace(cachedMessages);
        } else {
            messages = EvictingQueue.create(MAX_OLD_MESSAGES_FOR_CHAT);
            messages.add(message);
            cachedMessages = new Element(cacheKey, messages);
            chatMessageCache.put(cachedMessages);
        }
    }

    public static List<MessageToChat> getMessageForChat(final Long chatNumber) {
        final String cacheKey = String.format(CHAT_MESSAGES, chatNumber);
        final Element cachedMessages = chatMessageCache.get(cacheKey);
        if (cachedMessages != null) {
            final List<MessageToChat> messages = Lists.newArrayList(((EvictingQueue) cachedMessages.getObjectValue()).iterator());
            Collections.sort(messages, (o1, o2) -> o1.getSendDate().compareTo(o2.getSendDate()));
            return messages;
        }

        return Collections.emptyList();
    }

    public static void updateChatUserCache(final String username, final Long chatNumber) {
        chatUserCache.put(new Element(username, chatNumber));
    }

    public static void removeUserFromCache(final String username, final Long chatNumber) {
        chatUserCache.remove(new Element(username, chatNumber));
    }

    public static List<String> getChatUsers(final Long chatNumber) {
        final Results results = chatUserCache.createQuery().addCriteria(Query.VALUE.eq(chatNumber)).includeKeys().addOrderBy(Query.KEY, Direction.ASCENDING).execute();
        final List<String> usernames = new LinkedList<>();
        for (final Result result : results.all()) {
            usernames.add((String) result.getKey());
        }
        return usernames;
    }
}
