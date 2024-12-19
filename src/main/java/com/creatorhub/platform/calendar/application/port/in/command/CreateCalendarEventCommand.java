package com.creatorhub.platform.calendar.application.port.in.command;

import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.DateTimeRange;
import com.creatorhub.platform.calendar.domain.vo.EventDescription;
import com.creatorhub.platform.calendar.domain.vo.EventTitle;
public record CreateCalendarEventCommand(
        CalendarId calendarId,
        EventTitle title,
        EventDescription description,
        DateTimeRange timeRange
) {}