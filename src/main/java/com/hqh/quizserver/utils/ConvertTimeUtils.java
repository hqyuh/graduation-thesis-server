package com.hqh.quizserver.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static com.hqh.quizserver.constant.TestQuizzImplConstant.DD_MM_YYYY_HH_MM_SS_SSS;
import static com.hqh.quizserver.constant.TestQuizzImplConstant.ZONE_ID;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

public class ConvertTimeUtils {

    /***
     * convert from dd/MM/yyyy to timestamp in sql
     *
     * @param time
     * @return timestamp
     */
    public static Timestamp convertTime(String time) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter DATE_FORMAT =
                new DateTimeFormatterBuilder()
                        .appendPattern(DD_MM_YYYY_HH_MM_SS_SSS)
                        .parseDefaulting(HOUR_OF_DAY, now.getHour())
                        .parseDefaulting(MINUTE_OF_HOUR, now.getMinute())
                        .parseDefaulting(SECOND_OF_MINUTE, now.getSecond())
                        .parseDefaulting(NANO_OF_SECOND, now.getNano())
                        .toFormatter()
                        .withZone(ZoneId.of(ZONE_ID));
        LocalDateTime localDateTime = LocalDateTime.from(DATE_FORMAT.parse(time));

        return Timestamp.valueOf(localDateTime);
    }

    /***
     * convert timestamp in sql to dd/MM/yyyy
     *
     * @param dayOfBirth
     * @return string
     */
    public static String formatTimeDayOfBirth(Timestamp dayOfBirth) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dayOfBirth.toLocalDateTime().format(FORMATTER);
    }

}
