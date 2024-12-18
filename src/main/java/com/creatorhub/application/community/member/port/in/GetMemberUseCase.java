package com.creatorhub.application.community.member.port.in;

import com.creatorhub.application.community.member.dto.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetMemberUseCase {
    MemberResponse getMember(UUID id);
    MemberResponse getMemberByEmail(String email);
    Page<MemberResponse> getAllMembers(Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
