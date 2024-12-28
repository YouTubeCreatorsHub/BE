package com.creatorhub.platform.calendar.application;

import com.creatorhub.platform.calendar.adapter.in.web.dto.CalendarEventResponse;
import com.creatorhub.platform.calendar.adapter.in.websocket.CalendarWebSocketHandler;
import com.creatorhub.platform.calendar.adapter.in.websocket.dto.CalendarUpdatedResponse;
import com.creatorhub.platform.calendar.adapter.in.websocket.dto.EventDeletedResponse;
import com.creatorhub.platform.calendar.adapter.in.websocket.dto.WebSocketMessage;
import com.creatorhub.platform.calendar.application.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarEventListener {
    private final CalendarWebSocketHandler webSocketHandler;

    @EventListener
    public void handleCalendarEventCreated(CalendarEventCreated event) {
        log.debug("Handling calendar event created: {}", event);
        CalendarEventResponse response = CalendarEventResponse.from(event.getEvent());
        WebSocketMessage<CalendarEventResponse> message = WebSocketMessage.of(
                "CALENDAR_EVENT_CREATED",
                event.getCalendarId().toString(),
                response
        );
        webSocketHandler.notifyCalendarUpdate(message);
        log.debug("Sent websocket message: {}", message);
    }

    @EventListener
    public void handleCalendarEventUpdated(CalendarEventUpdated event) {
        CalendarEventResponse response = CalendarEventResponse.from(event.getEvent());
        WebSocketMessage<CalendarEventResponse> message = WebSocketMessage.of(
                "CALENDAR_EVENT_UPDATED",
                event.getCalendarId().toString(),
                response
        );
        webSocketHandler.notifyCalendarUpdate(message);
    }

    @EventListener
    public void handleCalendarEventDeleted(CalendarEventDeleted event) {
        WebSocketMessage<EventDeletedResponse> message = WebSocketMessage.of(
                "CALENDAR_EVENT_DELETED",
                event.getCalendarId().toString(),
                new EventDeletedResponse(event.getEventId().toString())
        );
        webSocketHandler.notifyCalendarUpdate(message);
    }

    @EventListener
    public void handleCalendarUpdated(CalendarUpdated event) {
        WebSocketMessage<CalendarUpdatedResponse> message = WebSocketMessage.of(
                "CALENDAR_UPDATED",
                event.getCalendarId().toString(),
                new CalendarUpdatedResponse(event.getTitle().getValue())
        );
        webSocketHandler.notifyCalendarUpdate(message);
    }

    @EventListener
    public void handleCalendarDeleted(CalendarDeleted event) {
        WebSocketMessage<Void> message = WebSocketMessage.of(
                "CALENDAR_DELETED",
                event.getCalendarId().toString(),
                null
        );
        webSocketHandler.notifyCalendarUpdate(message);
    }
}