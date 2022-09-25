package com.hqh.quizserver.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;

import static com.hqh.quizserver.constant.PatternConstant.periodPattern;
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
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(DD_MM_YYYY_HH_MM_SS_SSS)
                        .parseDefaulting(HOUR_OF_DAY, now.getHour())
                        .parseDefaulting(MINUTE_OF_HOUR, now.getMinute())
                        .parseDefaulting(SECOND_OF_MINUTE, now.getSecond())
                        .parseDefaulting(NANO_OF_SECOND, now.getNano())
                        .toFormatter()
                        .withZone(ZoneId.of(ZONE_ID));
        LocalDateTime localDateTime = LocalDateTime.from(dateTimeFormatter.parse(time));

        return Timestamp.valueOf(localDateTime);
    }

    /***
     * convert timestamp in sql to dd/MM/yyyy
     *
     * @param dayOfBirth
     * @return string
     */
    public static String formatTimeDayOfBirth(Timestamp dayOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dayOfBirth.toLocalDateTime().format(formatter);
    }

    /**
     * It takes a string like "1h30m" and returns the number of seconds in that period
     *
     * @param period The period of time to wait before the next execution.
     * @return The number of seconds since the epoch.
     */
    public static Long parsePeriod(String period) {
        if (period == null) return null;
        period = period.toLowerCase(Locale.ROOT);
        Matcher matcher = periodPattern.matcher(period);
        Instant instant = Instant.EPOCH;

        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String type = matcher.group(2);
            switch (type) {
                case "h":
                    instant = instant.plus(Duration.ofHours(num));
                    break;
                case "m":
                    instant = instant.plus(Duration.ofMinutes(num));
                    break;
                default:
                    break;
            }
        }
        return instant.getEpochSecond();
    }

}
