package com.creatorhub.domain.community.repository.search;

import lombok.Builder;
import lombok.Getter;
import com.creatorhub.domain.community.entity.Member.Role;
import java.time.LocalDateTime;

@Getter
@Builder
public class MemberSearchCondition {
    private String email;            // 이메일
    private String nickname;         // 닉네임
    private Role role;              // 역할
    private Boolean isEnabled;       // 활성화 여부
    private LocalDateTime startDate; // 가입일 시작
    private LocalDateTime endDate;   // 가입일 종료
    private Boolean isDeleted;       // 삭제 여부

    public static MemberSearchCondition byEmail(String email) {
        return MemberSearchCondition.builder()
                .email(email)
                .build();
    }

    public static MemberSearchCondition byNickname(String nickname) {
        return MemberSearchCondition.builder()
                .nickname(nickname)
                .build();
    }

    public static MemberSearchCondition byRole(Role role) {
        return MemberSearchCondition.builder()
                .role(role)
                .build();
    }
}