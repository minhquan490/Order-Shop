package com.bachlinh.order.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER;
    private static final DateTimeFormatter DATE_FORMATTER;

    static {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
        DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public static String convertOutputDateTime(Timestamp target) {
        return convertOutputDateTime(target.toLocalDateTime());
    }

    public static String convertOutputDateTime(LocalDateTime target) {
        return DATE_TIME_FORMATTER.format(target);
    }

    public static String convertOutputDate(LocalDate target) {
        return DATE_FORMATTER.format(target);
    }

    public static String convertOutputDate(Date target) {
        return convertOutputDate(target.toLocalDate());
    }

    public static String convertOutputDate(Timestamp target) {
        return convertOutputDate(target.toLocalDateTime().toLocalDate());
    }

    public static Timestamp calculateTimeRefreshTokenExpired(Instant timeCreated) {
        return Timestamp.from(Instant.ofEpochSecond(timeCreated.getEpochSecond() + 86400 * 365));
    }
}
