package com.creatorhub.platform.calendar.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CalendarId implements Serializable {
    private static final long serialVersionUID = 1L;

    UUID value;

    public static CalendarId newId() {
        return new CalendarId(UUID.randomUUID());
    }
}