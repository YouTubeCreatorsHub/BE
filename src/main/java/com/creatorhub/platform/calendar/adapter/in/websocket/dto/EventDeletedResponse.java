package com.creatorhub.platform.calendar.adapter.in.websocket.dto;

public record EventDeletedResponse(String eventId) {
    public static EventDeletedResponse from(String eventId) {
        return new EventDeletedResponse(eventId);
    }
}
