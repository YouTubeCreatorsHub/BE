package com.creatorhub.platform.calendar.application.event;

import com.creatorhub.platform.calendar.domain.event.DomainEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.CalendarTitle;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarUpdated implements DomainEvent {
    private final CalendarId calendarId;
    private final CalendarTitle title;
    private final LocalDateTime occurredAt;

    public CalendarUpdated(CalendarId calendarId, CalendarTitle title) {
        this.calendarId = calendarId;
        this.title = title;
        this.occurredAt = LocalDateTime.now();
    }
}
