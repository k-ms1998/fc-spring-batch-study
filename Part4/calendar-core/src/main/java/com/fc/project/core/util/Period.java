package com.fc.project.core.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
public class Period {

    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public Period(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt != null ? startAt : LocalDateTime.now();
        this.endAt = endAt != null ? endAt : this.startAt;
    }

    public static Period of(LocalDate date) {
        return new Period(date.atStartOfDay(), LocalDateTime.of(date, LocalTime.of(23, 59, 59, 99)));
    }

    public static Period of(LocalDateTime startAt, LocalDateTime endAt) {
        return new Period(startAt, endAt);
    }

    public static Period of(LocalDate startDate, LocalDate endDate) {
        return new Period(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.of(23, 59, 59, 999)));
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

    public boolean isOverlapped(Period period) {
        return isOverlapped(period.startAt, period.endAt);
    }
}
