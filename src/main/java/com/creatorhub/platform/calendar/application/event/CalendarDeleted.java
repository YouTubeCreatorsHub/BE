package com.creatorhub.platform.calendar.application.event;

import com.creatorhub.platform.calendar.domain.event.DomainEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarDeleted implements DomainEvent {
    private final CalendarId calendarId;
    private final LocalDateTime occurredAt;

    public CalendarDeleted(CalendarId calendarId) {
        this.calendarId = calendarId;
        this.occurredAt = LocalDateTime.now();
    }
}
