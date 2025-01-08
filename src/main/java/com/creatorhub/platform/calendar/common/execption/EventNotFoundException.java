package com.creatorhub.platform.calendar.common.execption;

import com.creatorhub.platform.calendar.domain.vo.CalendarEventId;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(CalendarEventId eventId) {
    }
}
