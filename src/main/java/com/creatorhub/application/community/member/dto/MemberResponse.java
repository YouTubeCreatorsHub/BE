package com.creatorhub.application.community.member.dto;

import com.creatorhub.domain.community.entity.Member;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MemberResponse {
    private final UUID id;
    private final String email;
    private final String nickname;
    private final Member.Role role;
    private final boolean isEnabled;
    private final long articleCount;
    private final long commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .isEnabled(member.isEnabled())
                .articleCount(member.getArticles().size())
                .commentCount(member.getComments().size())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .createdBy(member.getCreatedBy())
                .build();
    }
}