package com.creatorhub.platform.calendar.domain.vo;

public enum PermissionLevel {
    READ,
    WRITE,
    ADMIN;

    public boolean isAtLeast(PermissionLevel other) {
        return this.ordinal() >= other.ordinal();
    }
}
