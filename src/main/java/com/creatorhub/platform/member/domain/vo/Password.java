package com.creatorhub.platform.member.domain.vo;

public record Password(String value) {
    public Password {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}