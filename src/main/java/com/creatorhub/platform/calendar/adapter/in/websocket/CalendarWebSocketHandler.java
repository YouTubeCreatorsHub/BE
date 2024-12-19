package com.creatorhub.platform.calendar.adapter.in.websocket;

import com.creatorhub.platform.calendar.adapter.in.web.dto.CalendarEventResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String calendarId = extractCalendarId(session);
        sessions.put(calendarId, session);
        log.info("WebSocket connection established for calendar: {}", calendarId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 메시지 처리 로직
        log.debug("Received message: {}", message.getPayload());
    }

    public void notifyCalendarUpdate(String calendarId, CalendarEventResponse eventResponse) {
        WebSocketSession session = sessions.get(calendarId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(eventResponse)));
                log.debug("Calendar update notification sent for calendar: {}", calendarId);
            } catch (IOException e) {
                log.error("Failed to send message for calendar: {}", calendarId, e);
            }
        }
    }

    private String extractCalendarId(WebSocketSession session) {
        String path = session.getUri().getPath();
        // URI 패턴이 "/ws/calendar/{calendarId}"라고 가정
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            throw new IllegalArgumentException("Invalid WebSocket URL format");
        }
        return pathParts[3];  // /ws/calendar/{calendarId} 에서 calendarId 추출
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String calendarId = extractCalendarId(session);
        sessions.remove(calendarId);
        log.info("WebSocket connection closed for calendar: {}", calendarId);
    }
}