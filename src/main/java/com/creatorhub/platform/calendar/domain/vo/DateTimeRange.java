package com.creatorhub.platform.calendar.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class DateTimeRange {
    LocalDateTime start;
    LocalDateTime end;

    public DateTimeRange(LocalDateTime start, LocalDateTime end) {
        validateTimeRange(start, end);
        this.start = start;
        this.end = end;
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end times cannot be null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }
}