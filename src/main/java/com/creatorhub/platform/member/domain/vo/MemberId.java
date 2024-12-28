package com.creatorhub.platform.member.domain.vo;

public record MemberId(String value) {
    public MemberId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("MemberId cannot be empty");
        }
    }
}