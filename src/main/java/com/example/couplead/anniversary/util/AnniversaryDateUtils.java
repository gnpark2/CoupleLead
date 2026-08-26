package com.example.couplead.anniversary.util;

import com.example.couplead.anniversary.domain.RepeatType;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class AnniversaryDateUtils {

    private AnniversaryDateUtils() {
    }

    public static long calculateDDay(
            LocalDate anniversaryDate,
            RepeatType repeatType) {
        LocalDate today = LocalDate.now();

        LocalDate targetDate = resolveTargetDate(
                anniversaryDate,
                repeatType,
                today);

        return ChronoUnit.DAYS.between(
                today,
                targetDate);
    }

    public static LocalDate resolveTargetDate(
            LocalDate anniversaryDate,
            RepeatType repeatType,
            LocalDate today) {
        if (repeatType != RepeatType.YEARLY) {
            return anniversaryDate;
        }

        LocalDate targetDate = createSafeDate(
                today.getYear(),
                anniversaryDate.getMonthValue(),
                anniversaryDate.getDayOfMonth());

        if (targetDate.isBefore(today)) {
            targetDate = createSafeDate(
                    today.getYear() + 1,
                    anniversaryDate.getMonthValue(),
                    anniversaryDate.getDayOfMonth());
        }

        return targetDate;
    }

    private static LocalDate createSafeDate(
            int year,
            int month,
            int day) {
        try {
            return LocalDate.of(
                    year,
                    month,
                    day);
        } catch (DateTimeException e) {
            /*
             * 2월 29일이 평년인 경우
             * 2월 28일로 처리
             */
            if (month == 2 &&
                    day == 29) {
                return LocalDate.of(
                        year,
                        2,
                        28);
            }

            throw e;
        }
    }
}