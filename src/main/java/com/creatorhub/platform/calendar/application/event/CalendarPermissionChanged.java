package com.creatorhub.platform.calendar.application.event;

import com.creatorhub.platform.calendar.domain.event.DomainEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.PermissionLevel;
import com.creatorhub.platform.calendar.domain.vo.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarPermissionChanged implements DomainEvent {
    private final CalendarId calendarId;
    private final UserId userId;
    private final PermissionLevel level;
    private final LocalDateTime occurredAt;

    public CalendarPermissionChanged(CalendarId calendarId, UserId userId, PermissionLevel level) {
        this.calendarId = calendarId;
        this.userId = userId;
        this.level = level;
        this.occurredAt = LocalDateTime.now();
    }
}
