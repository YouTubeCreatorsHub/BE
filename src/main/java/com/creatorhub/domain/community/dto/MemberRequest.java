package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequest {
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(Member.Role.USER)
                .isEnabled(true)
                .build();
    }
}