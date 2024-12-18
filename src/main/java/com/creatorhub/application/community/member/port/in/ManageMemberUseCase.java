package com.creatorhub.application.community.member.port.in;

import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.community.member.dto.MemberResponse;
import java.util.UUID;

public interface ManageMemberUseCase {
    MemberResponse createMember(MemberCommand.Create command);
    MemberResponse updateMember(MemberCommand.Update command);
    MemberResponse updatePassword(MemberCommand.UpdatePassword command);
    void deleteMember(UUID id);
}