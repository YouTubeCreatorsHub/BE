package com.creatorhub.platform.calendar.adapter.in.websocket.dto;

public record CalendarUpdatedResponse(String title) {
    public static CalendarUpdatedResponse from(String title) {
        return new CalendarUpdatedResponse(title);
    }
}