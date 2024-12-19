package com.creatorhub.platform.calendar.adapter.in.websocket.dto;

import com.creatorhub.platform.calendar.adapter.in.web.dto.CalendarEventResponse;

public record WebSocketMessage(
        String type,
        String calendarId,
        CalendarEventResponse data
) {
    public static WebSocketMessage of(String type, String calendarId, CalendarEventResponse data) {
        return new WebSocketMessage(type, calendarId, data);
    }
}