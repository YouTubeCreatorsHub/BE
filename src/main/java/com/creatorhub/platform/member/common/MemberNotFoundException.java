package com.creatorhub.platform.member.common;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String memberId) {
        super("Member not found with id: " + memberId);
    }
}