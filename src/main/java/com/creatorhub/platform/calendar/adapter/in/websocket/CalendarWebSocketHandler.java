package com.creatorhub.platform.calendar.adapter.in.websocket;

import com.creatorhub.platform.calendar.adapter.in.websocket.dto.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        String calendarId = extractCalendarId(session);
        sessions.computeIfAbsent(calendarId, k -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("WebSocket {} connection established for calendar: {}, total sessions: {}",
                session, calendarId, sessions.get(calendarId).size());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        log.debug("Received message: {}", message.getPayload());
    }

    public <T> void notifyCalendarUpdate(WebSocketMessage<T> message) {
        Set<WebSocketSession> calendarSessions = sessions.get(message.calendarId());
        if (calendarSessions != null && !calendarSessions.isEmpty()) {
            String messageString;
            try {
                messageString = objectMapper.writeValueAsString(message);
            } catch (IOException e) {
                log.error("Failed to serialize message", e);
                return;
            }

            calendarSessions.removeIf(session -> {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(messageString));
                        return false; // 세션 유지
                    } catch (IOException e) {
                        log.error("Failed to send message to session: {}", session.getId(), e);
                    }
                }
                return true; // 세션 제거
            });

            log.debug("Calendar update notification sent for calendar: {}, type: {}, to {} sessions",
                    message.calendarId(), message.type(), calendarSessions.size());
        } else {
            log.warn("No active sessions found for calendar: {}", message.calendarId());
        }
    }

    private String extractCalendarId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("WebSocket URI cannot be null");
        }

        String path = uri.getPath();

        // URI 패턴이 "/ws/calendar/{calendarId}"라고 가정
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            throw new IllegalArgumentException("Invalid WebSocket URL format");
        }
        return pathParts[3];  // /ws/calendar/{calendarId} 에서 calendarId 추출
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String calendarId = extractCalendarId(session);
        Set<WebSocketSession> calendarSessions = sessions.get(calendarId);
        if (calendarSessions != null) {
            calendarSessions.remove(session);
            if (calendarSessions.isEmpty()) {
                sessions.remove(calendarId);
            }
            log.info("WebSocket connection closed for calendar: {}, remaining sessions: {}",
                    calendarId, calendarSessions.size());
        }
    }
}