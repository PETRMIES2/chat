package com.sope.sync.service.fi.mtv3;

import java.util.Date;

import com.google.common.base.Function;
import com.sope.domain.category.Category;
import com.sope.domain.category.CategoryType;
import com.sope.sync.service.fi.TvShowConverterUtil;
import com.sope.utils.DateTimeZoneUtils;

public class Mtv3TvGuideConverter implements Function<Mtv3ShowGuideDTO, Category> {

    @Override
    public Category apply(final Mtv3ShowGuideDTO mtv3Guide) {
        final Category tvShow = new Category();
        tvShow.setCompany(mtv3Guide.channel.toLowerCase());
        tvShow.setIconName(TvShowConverterUtil.getShowIconForPhone(mtv3Guide.channel));
        tvShow.setCreated(new Date());

        tvShow.setStartTime(DateTimeZoneUtils.convertToUTC(mtv3Guide.startTime, TvShowConverterUtil.TIMEZONE_HELSINKI));
        tvShow.setEndTime(DateTimeZoneUtils.convertToUTC(mtv3Guide.endTime, TvShowConverterUtil.TIMEZONE_HELSINKI));
        tvShow.setName(mtv3Guide.name);
        tvShow.setTimezone(TvShowConverterUtil.TIMEZONE_HELSINKI.getID());
        tvShow.setType(CategoryType.SHOW);
        tvShow.setOrdinal(TvShowConverterUtil.getShowOrdinal(mtv3Guide.channel));

        return tvShow;
    }

}