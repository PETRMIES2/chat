package com.sope.utils;

import java.util.Date;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class DateTimeZoneUtils {

    public static Date convertToTimeZone(Date date, String timezone) {
        return new LocalDateTime(DateTimeZone.forID(timezone).convertUTCToLocal(date.getTime())).toDate();
    }

    public static Date convertToUTC(Date time, DateTimeZone timezone) {
        return new Date(timezone.convertLocalToUTC(time.getTime(), false));
    }
}
