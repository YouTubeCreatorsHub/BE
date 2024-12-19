package com.creatorhub.platform.calendar.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class EventDescription {
    String value;

    public EventDescription(String value) {
        validateDescription(value);
        this.value = value;
    }

    private void validateDescription(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Event description cannot be null");
        }
    }
}
