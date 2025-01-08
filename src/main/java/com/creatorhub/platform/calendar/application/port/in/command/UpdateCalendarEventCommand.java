package com.creatorhub.platform.calendar.application.port.in.command;

import com.creatorhub.platform.calendar.domain.vo.*;

public record UpdateCalendarEventCommand(
        CalendarId calendarId,
        CalendarEventId eventId,
        EventTitle title,
        EventDescription description,
        DateTimeRange timeRange
) {}
