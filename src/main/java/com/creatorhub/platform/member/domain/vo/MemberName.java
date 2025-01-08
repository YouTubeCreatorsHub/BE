package com.creatorhub.platform.member.domain.vo;

public record MemberName(String value) {
    public MemberName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}