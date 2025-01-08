package com.creatorhub.platform.calendar.application.port.in.command;

import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.CalendarTitle;

public record UpdateCalendarCommand(
        CalendarId calendarId,
        CalendarTitle newTitle
) {
}
