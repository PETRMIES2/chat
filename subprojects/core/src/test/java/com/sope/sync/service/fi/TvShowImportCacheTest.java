package com.sope.sync.service.fi;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sope.domain.category.Category;
import com.sope.domain.category.TvShowImportCache;

public class TvShowImportCacheTest {

    private final TvShowImportCache importCache = new TvShowImportCache();

    @Test
    public void tvShowAlreadySaved() {
        addShow(buildShow("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00")));
        assertThat(importCache.showAlreadyPersisted("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00"))).isTrue();

    }

    @Test
    public void shouldNotFindTvShowForDifferentDay() {
        addShow(buildShow("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00")));
        assertThat(importCache.showAlreadyPersisted("mtv3", "akuutti", stringToDate("2016-09-18 15:00:00"))).isFalse();

    }

    @Test
    public void shouldFindShowWhenSavedSecondsDoNotMatch() {
        addShow(buildShow("mtv3", "akuutti", stringToDate("2016-09-17 15:00:05")));
        assertThat(importCache.showAlreadyPersisted("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00"))).isTrue();
    }

    @Test
    public void shouldFindShowWhenSourceSecondsDoNotMatch() {
        addShow(buildShow("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00")));
        assertThat(importCache.showAlreadyPersisted("mtv3", "akuutti", stringToDate("2016-09-17 15:00:06"))).isTrue();
    }

    @Test
    public void shouldFindShowWhenStartTimeAndNameAlmostMatch() {
        addShow(buildShow("mtv3", "akuutti (S)", stringToDate("2016-09-17 15:00:05")));
        assertThat(importCache.showAlreadyPersisted("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00"))).isTrue();
    }

    @Test
    public void shouldFindShowWhenNameIsTooDifferent() {
        addShow(buildShow("mtv3", "akuutti (S) Kuusamo", stringToDate("2016-09-17 15:00:05")));
        assertThat(importCache.showAlreadyPersisted("mtv3", "akuutti", stringToDate("2016-09-17 15:00:00"))).isFalse();
    }

    private void addShow(final Category buildShow) {
        importCache.setShows(Lists.newArrayList(buildShow));

    }

    private Category buildShow(final String company, final String showName, final Date startTime) {
        final Category tempShow = new Category();
        tempShow.setCompany(company);
        tempShow.setName(showName);
        tempShow.setStartTime(startTime);
        return tempShow;
    }

    private Date stringToDate(final String date) {
        return LocalDateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
    }

}
