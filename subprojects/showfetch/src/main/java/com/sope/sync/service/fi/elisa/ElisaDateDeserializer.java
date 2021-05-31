package com.sope.sync.service.fi.elisa;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ElisaDateDeserializer implements JsonDeserializer<Date> {
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        try {
            String dateTime = json.getAsString();
            return new SimpleDateFormat(DATE_FORMAT).parse(dateTime);
        } catch (ParseException e) {
            throw new RuntimeException("CANNOT CONVERT DATE ", e);
        }

    }
}
