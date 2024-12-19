package com.creatorhub.platform.calendar.adapter.in.web.dto;

import com.creatorhub.platform.calendar.domain.entity.Calendar;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record CalendarResponse(
        UUID id,
        String ownerId,
        String title,
        Set<CalendarEventResponse> events
) {
    public static CalendarResponse from(Calendar calendar) {
        return new CalendarResponse(
                calendar.getId().getValue(),
                calendar.getOwnerId().getValue(),
                calendar.getTitle().getValue(),
                calendar.getEvents().stream()
                        .map(CalendarEventResponse::from)
                        .collect(Collectors.toSet())
        );
    }
}