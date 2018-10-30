package com.kvo.utils.datetime;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 */
@Slf4j
public abstract class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static Date parseDate(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        try {
            Date date;
            if (source.indexOf('-') > -1) {
                date = parseDate(source, DateTimeStyle.parsePatternsHyphen);
            } else if (source.indexOf('/') > -1) {
                date = parseDate(source, DateTimeStyle.parsePatternsSlash);
            } else {
                date = parseDate(source, DateTimeStyle.parsePatterns);
            }
            return date;
        } catch (ParseException e) {
            log.error("parseDate", e);
            throw new RuntimeException(e);
        }
    }

    public static Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static Date getPervMonth() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.add(Calendar.MONTH, -1);
        return todayStart.getTime();
    }

    public static String currentDateFormat(String format) {
        return FastDateFormat.getInstance(format).format(new Date());
    }

    /**
     * 格式化时间
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return FastDateFormat.getInstance(pattern).format(date);
    }

    /**
     * 字符串转时间
     *
     * @param source
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date pares(String source, String pattern) throws ParseException {
        return FastDateFormat.getInstance(pattern).parse(source);
    }
}