package org.jekajops.payment_service.core.utils.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static final int SECOND = 1000;
    public static final int MINUTE = SECOND * 60;
    public static final int HOUR = MINUTE * 60;

    public static String getCurrentLocalTime(int addMinutes) {
        var sdf = new SimpleDateFormat("HH:mm");
        var date = new Date(System.currentTimeMillis() + addMinutes * MINUTE);
        return sdf.format(date);
    }

    public static long parseTimeUTCFromHistory(String timeUTCString) throws ParseException {
        timeUTCString = timeUTCString.replace("T", " ").replace("Z", "");
        var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var date = sdf.parse(timeUTCString);
        return date.getTime();
    }

    public static String timestampToDateUTCString(long ts, int addMinutes) {
        var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        var date = new Date(ts - 3 * HOUR + addMinutes * MINUTE);
        return sdf.format(date).replace(" ", "T")+"Z";
    }
}
