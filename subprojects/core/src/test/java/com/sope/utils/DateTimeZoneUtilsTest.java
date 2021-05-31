package com.sope.utils;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeZoneUtilsTest {

    @Test
    public void shouldConvertEuropeHelsinkiTimeToUTC() {
        LocalDateTime dateTime = new LocalDateTime(2016,12,31,1,0,3);
        LocalDateTime result = new LocalDateTime(DateTimeZoneUtils.convertToUTC(dateTime.toDate(), DateTimeZone.forID("Europe/Helsinki")));
        assertEquals(new LocalDateTime(2016,12,30,23,0,3), result);
    }

    @Test
    public void shouldConvertUTCToEuropeHelsinkiTime() {
        LocalDateTime dateTime = new LocalDateTime(2016,12,30,23,0,3);
        LocalDateTime result = new LocalDateTime(DateTimeZoneUtils.convertToTimeZone(dateTime.toDate(), "Europe/Helsinki"));
        assertEquals(new LocalDateTime(2016,12,31,1,0,3), result);
        
    }
}
