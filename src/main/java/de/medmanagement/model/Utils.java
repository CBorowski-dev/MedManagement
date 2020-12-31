package de.medmanagement.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Date normilizeDate(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            return date;
        }
    }

}
