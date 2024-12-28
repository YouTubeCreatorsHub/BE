package com.creatorhub.platform.calendar.adapter.in.websocket.dto;

public record WebSocketMessage<T>(
        String type,
        String calendarId,
        T data
) {
    public static <T> WebSocketMessage<T> of(String type, String calendarId, T data) {
        String uuid = calendarId.contains("CalendarId(value=")
                ? calendarId.replace("CalendarId(value=", "").replace(")", "")
                : calendarId;

        return new WebSocketMessage<>(type, uuid, data);    }
}