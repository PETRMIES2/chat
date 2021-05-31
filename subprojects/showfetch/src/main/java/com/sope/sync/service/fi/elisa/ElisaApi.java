package com.sope.sync.service.fi.elisa;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import okhttp3.OkHttpClient;

@Service
public class ElisaApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElisaApi.class);

    // to environments-parameter
    private static final String ELISA_API_URL = "http://api.elisaviihde.fi/etvrecorder/ajaxprograminfo.sl";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new ElisaDateDeserializer()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    Type type = new TypeToken<Map<String, List<Map<String, List<ElisaProgramsDTO>>>>>() {
    }.getType();

    private final TvShowService tvShowService;

    @Inject
    public ElisaApi(final TvShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    public void saveShows() {

        final List<Category> shows = new LinkedList<>();
        final List<ElisaProgramsDTO> elisaApiShows = getTvShows();
        shows.addAll(Lists.transform(elisaApiShows, new ElisaApiConverter()));

        final LocalDate now = LocalDate.now();

        tvShowService.saveShows(shows, now.toDate(), now.plusMonths(1).toDate(), TvShowConverterUtil.TIMEZONE_HELSINKI.getID());

    }

    private List<ElisaProgramsDTO> getTvShows() {
        try {
            //            InputStream testDate = new FileInputStream(new File("F:\\ohjelmointi\\sope\\elisaapi.json"));
            //            JsonReader reader = new JsonReader(new InputStreamReader(testDate, "UTF-8"));
            //            Map<String, List<Map<String, List<ElisaProgramsDTO>>>> values = gson.fromJson(reader, type);
            final Map<String, List<Map<String, List<ElisaProgramsDTO>>>> arrayFromElisa = tvShowService.queryDataFrom(ELISA_API_URL, gson, type);
            final List<ElisaProgramsDTO> shows = new LinkedList<>();
            for (final List<Map<String, List<ElisaProgramsDTO>>> source : arrayFromElisa.values()) {
                for (final Map<String, List<ElisaProgramsDTO>> channelInformation : source) {
                    for (final Entry<String, List<ElisaProgramsDTO>> information : channelInformation.entrySet()) {
                        for (final ElisaProgramsDTO show : information.getValue()) {
                            show.channel = normalize(URLDecoder.decode(information.getKey(), "UTF-8"));

                            if (TvShowConverterUtil.includeChannel(show.channel)) {
                                show.name = URLDecoder.decode(show.name, "UTF-8");
                                shows.add(show);
                            }
                        }
                    }
                }
            }
            return shows;
        } catch (final Exception e) {
            throw new RuntimeException("Cannot parse Elisa api shows", e);

        }
    }

    private String normalize(final String channel) {
        if (channel.toLowerCase().equals("yle tv1")) {
            return "yle1";
        }
        if (channel.toLowerCase().equals("yle tv2")) {
            return "yle2";
        }
        return channel;
    }

}
