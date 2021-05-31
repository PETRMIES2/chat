package com.sope.sync.service.fi.elisa;

import java.util.Date;

import com.google.common.base.Function;
import com.sope.domain.category.Category;
import com.sope.domain.category.CategoryType;
import com.sope.sync.service.fi.TvShowConverterUtil;
import com.sope.utils.DateTimeZoneUtils;

public class ElisaApiConverter implements Function<ElisaProgramsDTO, Category> {

    @Override
    public Category apply(final ElisaProgramsDTO elisaProgram) {
        final Category show = new Category();
        show.setCompany(elisaProgram.channel.toLowerCase());
        show.setIconName(TvShowConverterUtil.getShowIconForPhone(elisaProgram.channel));
        show.setCreated(new Date());
        show.setStartTime(DateTimeZoneUtils.convertToUTC(elisaProgram.startTime, TvShowConverterUtil.TIMEZONE_HELSINKI));
        show.setEndTime(DateTimeZoneUtils.convertToUTC(elisaProgram.endTime, TvShowConverterUtil.TIMEZONE_HELSINKI));
        show.setName(elisaProgram.name);
        show.setTimezone(TvShowConverterUtil.TIMEZONE_HELSINKI.getID());
        show.setType(CategoryType.SHOW);

        show.setOrdinal(TvShowConverterUtil.getShowOrdinal(elisaProgram.channel));

        return show;
    }

}