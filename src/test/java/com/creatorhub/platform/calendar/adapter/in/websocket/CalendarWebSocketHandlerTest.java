package com.creatorhub.platform.calendar.adapter.in.websocket;

import com.creatorhub.platform.calendar.adapter.in.web.dto.CalendarEventResponse;
import com.creatorhub.platform.calendar.domain.vo.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarWebSocketHandlerTest {

    @Mock
    private WebSocketSession session;

    @Mock
    private ObjectMapper objectMapper;

    private CalendarWebSocketHandler webSocketHandler;
    private String calendarId;

    @BeforeEach
    void setUp() {
        webSocketHandler = new CalendarWebSocketHandler(objectMapper);
        calendarId = UUID.randomUUID().toString();
    }

    @Nested
    @DisplayName("WebSocket 연결 테스트")
    class ConnectionTest {

        @Test
        @DisplayName("웹소켓 연결이 성공적으로 수립된다")
        void connectionEstablished() throws Exception {
            // Given
            given(session.getUri()).willReturn(new URI("/ws/calendar/" + calendarId));

            // When
            webSocketHandler.afterConnectionEstablished(session);

            // Then
            verify(session).getUri();
        }

        @Test
        @DisplayName("잘못된 URI 형식으로 연결 시도시 예외가 발생한다")
        void invalidUriThrowsException() throws Exception {
            // Given
            given(session.getUri()).willReturn(new URI("/ws/invalid"));

            // When & Then
            assertThatThrownBy(() ->
                    webSocketHandler.afterConnectionEstablished(session)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid WebSocket URL format");
        }
    }

    @Nested
    @DisplayName("메시지 전송 테스트")
    class MessageTest {

        @Test
        @DisplayName("캘린더 업데이트 알림이 성공적으로 전송된다")
        void notifyCalendarUpdate() throws IOException {
            // Given
            given(session.getUri()).willReturn(URI.create("/ws/calendar/" + calendarId));
            webSocketHandler.afterConnectionEstablished(session);

            CalendarEventResponse eventResponse = new CalendarEventResponse(
                    UUID.randomUUID(),
                    "Test Event",
                    "Test Description",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1),
                    EventStatus.SCHEDULED
            );

            String jsonMessage = "{\"test\":\"message\"}";
            given(objectMapper.writeValueAsString(eventResponse)).willReturn(jsonMessage);
            given(session.isOpen()).willReturn(true);

            // When
            webSocketHandler.notifyCalendarUpdate(calendarId, eventResponse);

            // Then
            verify(session).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("세션이 닫혀있으면 메시지가 전송되지 않는다")
        void doNotSendMessageWhenSessionClosed() throws IOException {
            // Given
            given(session.getUri()).willReturn(URI.create("/ws/calendar/" + calendarId));
            webSocketHandler.afterConnectionEstablished(session);
            given(session.isOpen()).willReturn(false);

            CalendarEventResponse eventResponse = mock(CalendarEventResponse.class);

            // When
            webSocketHandler.notifyCalendarUpdate(calendarId, eventResponse);

            // Then
            verify(session, never()).sendMessage(any(TextMessage.class));
        }
    }

    @Nested
    @DisplayName("연결 종료 테스트")
    class DisconnectionTest {

        @Test
        @DisplayName("웹소켓 연결이 정상적으로 종료된다")
        void connectionClosed() throws Exception {
            // Given
            given(session.getUri()).willReturn(URI.create("/ws/calendar/" + calendarId));
            webSocketHandler.afterConnectionEstablished(session);

            // When
            webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

            // Then
            // 이후 메시지 전송 시도시 전송되지 않아야 함
            CalendarEventResponse eventResponse = mock(CalendarEventResponse.class);
            webSocketHandler.notifyCalendarUpdate(calendarId, eventResponse);
            verify(session, never()).sendMessage(any(TextMessage.class));
        }
    }

    @Nested
    @DisplayName("메시지 수신 테스트")
    class MessageReceiveTest {

        @Test
        @DisplayName("텍스트 메시지가 정상적으로 수신된다")
        void handleTextMessage() throws Exception {
            // Given
            TextMessage message = new TextMessage("test message");

            // When
            webSocketHandler.handleTextMessage(session, message);

            // Then
            // 로그 확인이나 추가적인 메시지 처리 검증
            verify(session, never()).sendMessage(any(TextMessage.class)); // 현재는 응답을 보내지 않음
        }
    }
}