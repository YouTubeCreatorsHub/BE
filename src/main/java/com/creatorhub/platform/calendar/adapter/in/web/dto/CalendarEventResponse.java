package com.creatorhub.platform.calendar.adapter.in.web.dto;

import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.vo.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarEventResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        EventStatus status
) {
    public static CalendarEventResponse from(CalendarEvent event) {
        return new CalendarEventResponse(
                event.getId().getValue(),
                event.getTitle().getValue(),
                event.getDescription().getValue(),
                event.getTimeRange().getStart(),
                event.getTimeRange().getEnd(),
                event.getStatus()
        );
    }
}