package com.jaka.recyclerviewapplication.model.converters;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.room.TypeConverter;

public class DateTimeConverters {

    private static final DateFormat DATE_WITH_YEAR_FORMAT = new SimpleDateFormat("dd MM, yyyy HH:mm:ss", Locale.getDefault());

    @TypeConverter
    public Date toDate(String date) {
        try {
            if (!TextUtils.isEmpty(date)) {
                return DATE_WITH_YEAR_FORMAT.parse(date);
            } else {
                return new Date();
            }
        } catch (Throwable throwable) {
            Log.e("TEST", "PARSE THROW EXCEPTION", throwable);
        }
        return new Date();
    }

    @TypeConverter
    public String toString(Date date) {
        if (date != null) {
            return DATE_WITH_YEAR_FORMAT.format(date);
        } else {
            return "";
        }
    }
}
