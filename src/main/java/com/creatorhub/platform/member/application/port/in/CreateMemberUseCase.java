package com.creatorhub.platform.member.application.port.in;

import com.creatorhub.platform.member.domain.entity.Member;

public interface CreateMemberUseCase {
    Member createMember(CreateMemberCommand command);
}