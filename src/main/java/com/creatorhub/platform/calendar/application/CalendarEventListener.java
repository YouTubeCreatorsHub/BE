package com.creatorhub.platform.calendar.application;

import com.creatorhub.platform.calendar.adapter.in.web.dto.CalendarEventResponse;
import com.creatorhub.platform.calendar.adapter.in.websocket.CalendarWebSocketHandler;
import com.creatorhub.platform.calendar.application.event.CalendarEventCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarEventListener {
    private final CalendarWebSocketHandler webSocketHandler;

    @EventListener
    public void handleCalendarEventCreated(CalendarEventCreated event) {
        CalendarEventResponse response = CalendarEventResponse.from(event.getEvent());
        webSocketHandler.notifyCalendarUpdate(
                event.getCalendarId().toString(),
                response
        );
    }
}