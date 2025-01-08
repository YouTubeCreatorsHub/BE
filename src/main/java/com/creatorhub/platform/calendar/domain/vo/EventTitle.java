package com.creatorhub.platform.calendar.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class EventTitle {
    String value;

    public EventTitle(String value) {
        validateTitle(value);
        this.value = value;
    }

    private void validateTitle(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be empty");
        }
    }
}