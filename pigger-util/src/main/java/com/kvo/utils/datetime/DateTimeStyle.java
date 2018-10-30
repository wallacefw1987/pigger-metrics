package com.kvo.utils.datetime;

import com.kvo.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间格式类
 */
public abstract class DateTimeStyle {

    protected final static Logger LOGGER = LoggerFactory.getLogger(DateTimeStyle.class);

    public final static String DEFAULT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String DEFAULT_YYYY_MM_DD = "yyyy-MM-dd";
    public final static String DEFAULT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String[] parsePatterns = {
            DEFAULT_YYYYMMDDHHMMSS,
            "yyyyMMdd",
            "yyyy年MM月dd日",
            "yyyy.MM.ddHH:mm:ss",
            "yyyyMM",
            "yyyy年MM月",
            "yyyy",
            "yyyy年M月",
            "yyyy年MM月dd"
    };

    public static String[] parsePatternsHyphen = {
            DEFAULT_YYYY_MM_DD_HH_MM_SS,
            "yyyy-MM-dd HH:mm",
            DEFAULT_YYYY_MM_DD,
            "yyyy-MM",
            "yyyy-M-dd",
            "yyyy-MM-d",
            "yyyy-M-d HH:mm:ss",
            "yyyy-MM-d HH:mm:ss",
            "yyyy-M-d",
            "yyyy-M-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-ddHH:mm:ss"
    };

    public static String[] parsePatternsSlash = {
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd"
    };

    private static String[] parsePatterns1 = {
            "yyyy-MM", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy-M-dd",
            "yyyy-MM-d",
            "yyyy-M-d HH:mm:ss",
            "yyyy-MM-d HH:mm:ss",
            "yyyy-M-d",
            "yyyy-M-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-ddHH:mm:ss"
    };

    private static String[] parsePatterns2 = {
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd"
    };

    public static Date parseDate(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            Date date;
            if (str.indexOf('-') > -1) {
                date = DateUtils.parseDate(str.toString(), parsePatterns1);
            } else if (str.indexOf('/') > -1) {
                date = DateUtils.parseDate(str.toString(), parsePatterns2);
            } else {
                date = DateUtils.parseDate(str.toString(), parsePatterns);
            }

            return date;
        } catch (ParseException e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public static Long getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    public static Date getPervMonth() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.add(Calendar.MONTH, -1);
        return todayStart.getTime();
    }

    public static String timestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    public static String currentDateFormat(String formate) {
        return new SimpleDateFormat(formate).format(new Date());
    }

}
