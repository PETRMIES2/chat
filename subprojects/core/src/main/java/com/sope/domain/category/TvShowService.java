package com.sope.domain.category;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sope.domain.EntityRepository;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class TvShowService {
    private final OkHttpClient client = new OkHttpClient();

    private final EntityRepository entityRepository;
    private final CategoryRepository categoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TvShowService.class);

    @Inject
    public TvShowService(final EntityRepository entityRepository, final CategoryRepository showRepository) {
        this.entityRepository = entityRepository;
        this.categoryRepository = showRepository;
    }

    // FIXME make batch save
    public void saveShows(final List<Category> shows, final Date from, final Date to, final String timezone) {
        final TvShowImportCache importCache = new TvShowImportCache();
        importCache.setShows(categoryRepository.getShows(from, to, timezone));

        int i = 0;
        final int foundShows = shows.size();
        LOGGER.info("Ohjelmatietoja löytyi " + foundShows);
        final List<Category> saveableShows = new LinkedList<>();
        for (final Category tvShow : shows) {
            ++i;
            if (!importCache.showAlreadyPersisted(tvShow.getCompany(), tvShow.getName(), tvShow.getStartTime())) {
                saveableShows.add(tvShow);
            }
            if (i%20 == 0) {
                LOGGER.info("Comparing existing shows" + i + " / " + foundShows);
            }
        }
        entityRepository.statelessSave(saveableShows);
    }

    public <T> T queryDataFrom(final String url, final Gson gson, final Type type) {
        try {
            LOGGER.info("Sync from " + url);

            final Request request = new Request.Builder().url(url).build();

            final Response response = client.newCall(request).execute();
            return gson.fromJson(response.body().string(), type);

        } catch (final IOException e) {
            LOGGER.info("Cannot fetch shows", e);
        }
        return null;
    }

}
