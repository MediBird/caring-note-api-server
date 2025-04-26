package com.springboot.api.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public LocalDateTime parseToDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid date format: " + dateTime + ". Expected format: yyyy-MM-dd HH:mm");
        }
    }

    public static int calculateKoreanAge(LocalDate birthDate, LocalDate currentDate) throws RuntimeException {

        if (birthDate == null || currentDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        Period period = Period.between(birthDate, currentDate);

        int age = period.getYears();

        // 만나이 계산: 생일이 지나지 않았다면 나이에서 1을 뺌
        if (birthDate.plusYears(age).isAfter(currentDate)) {
            age--;
        }

        return age;
    }

    public String msToHMS(long ms) {
        long totalSeconds = ms / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

}
