package com.creatorhub.platform.calendar.application.port.in.command;

import com.creatorhub.platform.calendar.domain.vo.CalendarId;

public record DeleteCalendarCommand(CalendarId calendarId) {
}
