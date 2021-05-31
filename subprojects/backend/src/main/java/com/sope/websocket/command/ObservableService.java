package com.sope.websocket.command;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sope.domain.popular.PopularUpdate;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.SopeSocketMessage;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

@Service
public class ObservableService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservableService.class);

    // Tutkittava tarkemmin, onko tämä oikea luokka
    /*
     * AsyncSubject: only emits the last value of the source Observable
     *
     * BehaviorSubject: emits the most recently emitted item and all the
     * subsequent items of the source Observable when a observer subscribe to it
     *
     * PublishSubject: emits all the subsequent items of the source Observable
     * at the time of the subscription
     *
     * ReplaySubject: emits all the items of the source Observable, regardless
     * of when the subscriber subscribes.
     */
    // private BehaviorSubject<SopeWebSocketMessage> subject =
    // BehaviorSubject.create();
    //
    //
    // public BehaviorSubject<SopeWebSocketMessage> getObservable() {
    // return subject;
    // }

    private static final String ANDROID_GLOBAL = "ANDROID_GLOBAL";
    private final Map<String, PublishSubject<SopeSocketMessage>> generalObservables = new ConcurrentHashMap<>();
    private final Map<Long, ReplaySubject<SopeSocketMessage>> chatObservables = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, PopularUpdate> popularCategories = new ConcurrentHashMap<>();

    // Tyhmä rakenne, mutta fast fix. Jos etenee tehdään paremmin.
    private final ConcurrentHashMap<Long, Long> linkingPopularAndChatForCleaning = new ConcurrentHashMap<>();

    @PostConstruct
    private void createObservables() {
        generalObservables.put(ANDROID_GLOBAL, PublishSubject.create());
    }

    public PublishSubject<SopeSocketMessage> getGlobalObservable() {
        return generalObservables.get(ANDROID_GLOBAL);
    }

    public ReplaySubject<SopeSocketMessage> subscribeChat(final Long chatNumber) {

        if (!chatObservables.containsKey(chatNumber)) {
            chatObservables.put(chatNumber, ReplaySubject.create());
        }
        return chatObservables.get(chatNumber);
    }

    public Optional<ReplaySubject<SopeSocketMessage>> getChatSubscription(final Long chatNumber) {
        if (chatObservables.containsKey(chatNumber)) {
            return Optional.of(chatObservables.get(chatNumber));
        }
        chatObservables.put(chatNumber, ReplaySubject.create());
        return Optional.of(chatObservables.get(chatNumber));
    }

    public Consumer<CategoryDTO> addOneToUserInPopular() {
        return category -> {
            final PopularUpdate popularUpdate = getPopularCategory(category);
            popularUpdate.addOneToUserCount();
            popularCategories.put(category.getId(), popularUpdate);
            System.out.println("Updating popular user (+) count: " + popularUpdate);
        };
    }

    public Consumer<CategoryDTO> decreaseOneFromUserInPopular() {
        return category -> {
            final PopularUpdate popularUpdate = getPopularCategory(category);
            popularUpdate.decreaseUserCountByOne();
            popularCategories.put(category.getId(), popularUpdate);
            System.out.println("Updating popular user (-) count: " + popularUpdate);
        };
    }

    private PopularUpdate getPopularCategory(final CategoryDTO category) {
        if (popularCategories.containsKey(category.getId())) {
            return popularCategories.get(category.getId());
        }
        return new PopularUpdate(category, LocalDateTime.now());
    }

    public void updatePopular(final Long chatNumber, final CategoryDTO category, final Consumer<CategoryDTO> addOrDecrease) {
        addOrDecrease.accept(category);
        linkingPopularAndChatForCleaning.putIfAbsent(chatNumber, category.getId());
    }

    public Optional<ReplaySubject<SopeSocketMessage>> getChatSubscriptionForOldMessages(final Long chatNumber) {

        if (chatObservables.containsKey(chatNumber)) {
            return Optional.of(chatObservables.get(chatNumber));
        }
        chatObservables.put(chatNumber, ReplaySubject.create());
        return Optional.of(chatObservables.get(chatNumber));
    }

    public void cleanChatObservables() {
        chatObservables.forEach((key, value) -> {
            if (!chatObservables.get(key).hasObservers()) {
                LOGGER.info("Memory release: Removing chat subscription ChatId " + key);
                System.out.println("Memory release: Removing chat subscription ChatId " + key);
                chatObservables.remove(key);

                final Long categoryId = linkingPopularAndChatForCleaning.get(key);
                if (categoryId != null) {
                    final PopularUpdate popularUpdate = popularCategories.get(categoryId);
                    System.out.println("Memory release: check if popular category is ready for release " + popularUpdate);
                    popularUpdate.decreaseUserCountByOne();
                    if (popularUpdate.isReadyForRelease()) {
                        System.out.println("Memory release: removing user from popularity-structure " + popularUpdate);
                        popularCategories.remove(categoryId);
                        linkingPopularAndChatForCleaning.remove(key);
                    }
                }

            }
        });
    }

    public List<CategoryDTO> getCategoriesByPopularity() {
        return popularCategories.values().stream().map(p -> p.category).sorted((category, otherCategory) -> {
            if (category.getUsers() <= otherCategory.getUsers()) {
                return 1;
            }
            return -1;
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return chatObservables.toString();
    }
}
