package com.creatorhub.platform.calendar.application.event;

import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.event.DomainEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarEventUpdated implements DomainEvent {
    private final CalendarId calendarId;
    private final CalendarEvent event;
    private final LocalDateTime occurredAt;

    public CalendarEventUpdated(CalendarId calendarId, CalendarEvent event) {
        this.calendarId = calendarId;
        this.event = event;
        this.occurredAt = LocalDateTime.now();
    }
}
