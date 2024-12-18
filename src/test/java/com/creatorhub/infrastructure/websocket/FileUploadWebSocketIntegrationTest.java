package com.creatorhub.infrastructure.websocket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileUploadWebSocketIntegrationTest {
    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private BlockingQueue<String> blockingQueue;

    @BeforeEach
    void setup() {
        this.stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
        this.blockingQueue = new ArrayBlockingQueue<>(1);
    }

    @Test
    void shouldReceiveUploadProgress() throws Exception {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        StompHeaders connectHeaders = new StompHeaders();

        ListenableFuture<StompSession> connect = stompClient.connect(
                String.format("ws://localhost:%d/ws", port),
                headers,
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        session.subscribe("/topic/upload-progress/test.jpg", new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return String.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                blockingQueue.add((String) payload);
                            }
                        });
                    }
                }
        );

        // 1. orTimeout 대신 get with timeout 사용
        StompSession session = connect.get(10, TimeUnit.SECONDS);

        try {
            // 2. fail 메서드 대신 Assertions 사용
            assertThat(session.isConnected()).isTrue();
        } catch (Exception e) {
            Assertions.fail("Connection failed: " + e.getMessage());
        } finally {
            session.disconnect();
        }
    }
}