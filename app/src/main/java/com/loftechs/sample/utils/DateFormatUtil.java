package com.loftechs.sample.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatUtil {

    public static String getStringFormat(Long timestamp, String formatPattern) {
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat(formatPattern, Locale.getDefault());
        return formatter.format(date);
    }
}
