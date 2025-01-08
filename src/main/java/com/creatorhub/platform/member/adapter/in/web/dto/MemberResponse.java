package com.creatorhub.platform.member.adapter.in.web.dto;

import com.creatorhub.platform.member.domain.entity.Member;
import com.creatorhub.platform.member.domain.vo.MemberStatus;

public record MemberResponse(
        String id,
        String name,
        MemberStatus status
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId().value(),
                member.getName().value(),
                member.getStatus()
        );
    }
}
