package com.creatorhub.platform.calendar.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class CalendarPermission {
    UserId userId;
    PermissionLevel level;

    public CalendarPermission(UserId userId, PermissionLevel level) {
        this.userId = userId;
        this.level = level;
    }
}