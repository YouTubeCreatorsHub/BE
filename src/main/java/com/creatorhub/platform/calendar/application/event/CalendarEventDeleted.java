package com.creatorhub.platform.calendar.application.event;

import com.creatorhub.platform.calendar.domain.event.DomainEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarEventId;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarEventDeleted implements DomainEvent {
    private final CalendarId calendarId;
    private final CalendarEventId eventId;
    private final LocalDateTime occurredAt;

    public CalendarEventDeleted(CalendarId calendarId, CalendarEventId eventId) {
        this.calendarId = calendarId;
        this.eventId = eventId;
        this.occurredAt = LocalDateTime.now();
    }
}
