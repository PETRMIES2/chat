package com.sope.domain.category;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.sope.domain.EntityRepository;

@Service
public class CategoryRepository {
    private final EntityRepository entityRepository;

    @Inject
    public CategoryRepository(final EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public List<Category> getEvents() {
        final String hql = "from Category where type = :type and :currentTime between validFrom and validTo order by startTime";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("currentTime", new Date())
                .put("type", CategoryType.EVENT)
                .build();

        return entityRepository.get(hql, parameters);
    }
    public List<Category> getGeneral() {
        final String hql = "from Category where type = :type order by ordinal";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("type", CategoryType.GENERAL)
                .build();

        return entityRepository.get(hql, parameters);
    }

    public Optional<Category> getEventByName(final String eventName) {
        final String hql = "from Category where type = :type and name = :eventName and :currentTime between validFrom and validTo";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("currentTime", new Date())
                .put("eventName", eventName)
                .put("type", CategoryType.EVENT)
                .build();

        final List<Category> events = entityRepository.get(hql, parameters);

        if (events.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(events.get(0));

    }
    public Optional<Category> getGeneralByName(final String categoryName) {
        return getCategoryByName(categoryName, CategoryType.GENERAL);

    }
    // FIXME yhdistä koodia
    private Optional<Category> getCategoryByName(final String categoryName, final CategoryType type) {
        final String hql = "from Category where type = :type and name = :categoryName";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("categoryName", categoryName)
                .put("type", type)
                .build();

        final List<Category> category = entityRepository.get(hql, parameters);

        if (category.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(category.get(0));
    }

    public List<Category> getShows() {
        final String hql = "from Category where type = :type and :currentTime between startTime and endTime and endTime != :currentTime order by ordinal";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("currentTime", new Date())
                .put("type", CategoryType.SHOW)
                .build();

        // TODO haetaanko tässä myös käyttäjien lukumääärä?
        return entityRepository.get(hql, parameters);
    }

    public Optional<Category> getShowByName(final String showName) {
        final String hql = "from Category where type = :type and name = :showName and :currentTime between startTime and endTime and endTime != :currentTime order by ordinal";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("currentTime", new Date())
                .put("showName", showName)
                .put("type", CategoryType.SHOW)
                .build();

        final List<Category> shows = entityRepository.get(hql, parameters);

        if (shows.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(shows.get(0));


    }

    public Optional<Category> getBy(final String company, final Date startTime, final String name) {
        // Ei ota huomioon sitä, että ohjelmatiedot voivat muuttua
        final String hql = "from Category where type = :type and name = :showName and startTime = :startTime and company = :company";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("startTime", startTime)
                .put("showName", name)
                .put("company", company)
                .put("type", CategoryType.SHOW)
                .build();

        return entityRepository.getUnique(hql, parameters);

    }

    public List<Category> getShows(final Date from, final Date to, final String timezone) {
        final String hql = "from Category where type = :type and startTime between :from and :to and timezone = :timezone";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("from", from)
                .put("to", to)
                .put("timezone", timezone)
                .put("type", CategoryType.SHOW)
                .build();
        return entityRepository.get(hql, parameters);
    }

    public Integer getActiveChatters(final Category category) {
        final String hql = "select sum(userCount) from Chat where category = :category";
        final Map<Object, Object> parameters = new ImmutableMap.Builder<>()
                .put("category", category)
                .build();
        final Optional<Object> count = entityRepository.getUnique(hql, parameters);
        if (count.isPresent()) {
            return ((Long) count.get()).intValue();
        }
        return 0;
    }

}
