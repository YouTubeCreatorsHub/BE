package com.creatorhub.infrastructure.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

public class StompMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    public StompMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object fromMessage(Message<?> message, Class<?> targetClass) {
        try {
            String payload = new String((byte[]) message.getPayload());
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert message", e);
        }
    }

    @Override
    public Message<?> toMessage(Object payload, MessageHeaders headers) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return new GenericMessage<>(json.getBytes(), headers);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert message", e);
        }
    }
}