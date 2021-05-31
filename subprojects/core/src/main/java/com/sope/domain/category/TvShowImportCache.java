package com.sope.domain.category;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class TvShowImportCache {

	private static final int MAX_DIFF_FOR_NAME = 6;
	private static final int MAX_DIFF_FOR_START_TIME_MINUTES = 11;
	final ListMultimap<String, Category> shows = ArrayListMultimap.create();

	public void setShows(final List<Category> fetchedShows) {
		for (final Category tvShow : fetchedShows) {
			shows.put(tvShow.getCompany(), tvShow);
		}
	}

	public boolean showAlreadyPersisted(final String company, final String name, final Date startTime) {
		final List<Category> companyShows = shows.get(company);

		for (final Category tvShow : companyShows) {
			if (showNamesAreAlmostEqual(tvShow.getName(), name)
					&& startTimesWithinMinutes(tvShow.getStartTime(), startTime)) {
				return true;
			}
		}

		return false;

	}

	private boolean showNamesAreAlmostEqual(final String source, final String compare) {
		return StringUtils.getLevenshteinDistance(source.toLowerCase(), compare.toLowerCase()) <= MAX_DIFF_FOR_NAME;
	}

	private boolean startTimesWithinMinutes(final Date sourceStartTime, final Date compareStartTime) {
		final LocalDateTime source = LocalDateTime.fromDateFields(sourceStartTime).withSecondOfMinute(0).withMillisOfSecond(0);
		final LocalDateTime target = LocalDateTime.fromDateFields(compareStartTime).withSecondOfMinute(0).withMillisOfSecond(0);

		return Minutes.minutesBetween(source, target).getMinutes() <= MAX_DIFF_FOR_START_TIME_MINUTES;
	}
}
