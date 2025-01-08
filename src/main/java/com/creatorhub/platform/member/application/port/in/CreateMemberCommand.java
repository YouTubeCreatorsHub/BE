package com.creatorhub.platform.member.application.port.in;

import com.creatorhub.platform.member.domain.entity.Member;
import com.creatorhub.platform.member.domain.vo.MemberId;
import com.creatorhub.platform.member.domain.vo.MemberName;
import com.creatorhub.platform.member.domain.vo.Password;

public record CreateMemberCommand(
        String id,
        String name,
        String password
) {
    public Member toDomain() {
        return Member.create(
                new MemberId(id),
                new MemberName(name),
                new Password(password)
        );
    }
}

