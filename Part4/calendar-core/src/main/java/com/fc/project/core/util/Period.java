package com.fc.project.core.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class Period {

    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public static Period of(LocalDateTime startAt, LocalDateTime endAt) {
        return new Period(startAt, endAt);
    }

    public boolean isOverlapped(LocalDate date) {
        return this.isOverlapped(
                date.atStartOfDay(), LocalDateTime.of(date, LocalTime.of(23, 59, 59, 999))
        );
    }

    public boolean isOverlapped(LocalDateTime startAt, LocalDateTime endAt) {
        return this.startAt.isBefore(endAt)
                && startAt.isBefore(this.endAt);
    }
}
