package com.creatorhub.platform.calendar.domain.entity;

import com.creatorhub.platform.calendar.domain.vo.*;
import lombok.Getter;

@Getter
public class CalendarEvent {
    private final CalendarEventId id;
    private EventTitle title;
    private EventDescription description;
    private DateTimeRange timeRange;
    private EventStatus status;

    private CalendarEvent(CalendarEventId id, EventTitle title, EventDescription description,
                          DateTimeRange timeRange) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeRange = timeRange;
        this.status = EventStatus.SCHEDULED;
    }

    public static CalendarEvent create(EventTitle title, EventDescription description,
                                       DateTimeRange timeRange) {
        return new CalendarEvent(CalendarEventId.newId(), title, description, timeRange);
    }

    public static CalendarEvent reconstruct(CalendarEventId id, EventTitle title,
                                            EventDescription description, DateTimeRange timeRange,
                                            EventStatus status) {
        CalendarEvent event = new CalendarEvent(id, title, description, timeRange);
        event.status = status;
        return event;
    }

    public void update(EventTitle title, EventDescription description, DateTimeRange timeRange) {
        this.title = title;
        this.description = description;
        this.timeRange = timeRange;
    }

    public void complete() {
        this.status = EventStatus.COMPLETED;
    }

    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }
}