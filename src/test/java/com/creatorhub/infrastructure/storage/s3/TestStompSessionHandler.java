package com.creatorhub.infrastructure.storage.s3;

import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class TestStompSessionHandler extends StompSessionHandlerAdapter {
    private final CompletableFuture<Boolean> completableFuture;

    public TestStompSessionHandler(CompletableFuture<Boolean> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/progress/*", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 메시지 처리
            }
        });
        completableFuture.complete(true);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        completableFuture.completeExceptionally(exception);
    }
}