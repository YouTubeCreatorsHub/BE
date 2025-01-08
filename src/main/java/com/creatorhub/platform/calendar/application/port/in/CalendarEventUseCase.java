package com.creatorhub.platform.calendar.application.port.in;

import com.creatorhub.platform.calendar.application.port.in.command.*;
import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.vo.UserId;

import java.util.List;

public interface CalendarEventUseCase {
    CalendarEvent createEvent(CreateCalendarEventCommand command, UserId userId);
    CalendarEvent getEvent(GetEventCommand command, UserId userId);
    List<CalendarEvent> getAllEvent(GetAllEventCommand command, UserId userId);
    CalendarEvent updateEvent(UpdateCalendarEventCommand command, UserId of);
    void deleteEvent(DeleteCalendarEventCommand command, UserId userId);
}
