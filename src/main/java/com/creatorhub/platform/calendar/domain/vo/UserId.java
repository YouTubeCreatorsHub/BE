package com.creatorhub.platform.calendar.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class UserId {
    String value;

    public UserId(String value) {
        Objects.requireNonNull(value, "User ID cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.value = value;
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID().toString());
    }
}