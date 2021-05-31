package com.sope.sync.service.fi.mtv3;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sope.domain.category.Category;
import com.sope.domain.category.TvShowService;
import com.sope.sync.service.fi.TvShowConverterUtil;

@Service
public class Mtv3TvGuide {
    private static final int ONE_WEEK_LIMIT = 7;

    private static final Logger LOGGER = LoggerFactory.getLogger(Mtv3TvGuide.class);
    private static final String MTV3_TVOPAS_URL = "http://www.mtv.fi/asset/data/kanavaopas/tvopas-%s-lite.json";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Mtv3DateDeserializer()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private final Type mtv3ListType = new TypeToken<ArrayList<Mtv3ShowGuideDTO>>() {
    }.getType();

    private final TvShowService tvShowService;

    @Inject
    public Mtv3TvGuide(final TvShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    // FIXME hidas. Optimoi eli batch tallennus
    public void saveShows() {
        // final List<Mtv3ShowGuideDTO> showsFromMtv3Api = getShows();
        // printInfo(showsFromMtv3Api);
        final LocalDate now = LocalDate.now();
        final List<Category> shows = new LinkedList();
        for (int dayIndex = 0; dayIndex < ONE_WEEK_LIMIT; ++dayIndex) {
            final List<Mtv3ShowGuideDTO> fetchedShows = getShows(now.plusDays(dayIndex));
            shows.addAll(Lists.transform(fetchedShows, new Mtv3TvGuideConverter()));
        }
        tvShowService.saveShows(shows, now.toDate(), now.plusDays(ONE_WEEK_LIMIT).toDate(), TvShowConverterUtil.TIMEZONE_HELSINKI.getID());

    }

    private List<Mtv3ShowGuideDTO> getShows(final LocalDate fetchDay) {
        final String url = String.format(MTV3_TVOPAS_URL, getCurrentDate(fetchDay.toDate(), "yyyMMdd"));

        final List<Mtv3ShowGuideDTO> fetchedShows = tvShowService.queryDataFrom(url, gson, mtv3ListType);
        return filter(fetchedShows);
    }

    private List<Mtv3ShowGuideDTO> filter(final List<Mtv3ShowGuideDTO> fetchedShows) {
        final List<Mtv3ShowGuideDTO> includeShow = new LinkedList<>();

        for (final Mtv3ShowGuideDTO mtv3ShowGuideDTO : fetchedShows) {
            if (TvShowConverterUtil.includeChannel(mtv3ShowGuideDTO.channel)) {
                includeShow.add(mtv3ShowGuideDTO);
            }
        }
        return includeShow;
    }

    private void printInfo(final List<Mtv3ShowGuideDTO> showsFromMtv3Api) {
        final Set<String> tvNames = new HashSet<>();
        final SimpleDateFormat HH_MM = new SimpleDateFormat("HH:mm");
        for (final Mtv3ShowGuideDTO mtv3Guide : showsFromMtv3Api) {
            System.out.println(HH_MM.format(mtv3Guide.startTime) + "-" + HH_MM.format(mtv3Guide.endTime) + " " + mtv3Guide.channel + ":" + mtv3Guide.name);
            tvNames.add(mtv3Guide.channel);
        }
        for (final String name : tvNames) {
            System.out.println(".put(\"" + name + "\", 0)");

        }
    }

    private String getCurrentDate(final Date date, final String format) {
        return new SimpleDateFormat(format).format(date);
    }
}
