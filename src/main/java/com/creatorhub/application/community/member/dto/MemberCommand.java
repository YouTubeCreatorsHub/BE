package com.creatorhub.application.community.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

public class MemberCommand {

    @Getter
    @Builder
    public static class Create {
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 8-20자의 영문자, 숫자, 특수문자를 포함해야 합니다"
        )
        private String password;

        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다")
        private String nickname;

        @Builder.Default
        private boolean isEnabled = true;
    }

    @Getter
    @Builder
    public static class Update {
        private UUID id;

        @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다")
        private String nickname;

        private Boolean isEnabled;

        public Update withId(UUID id) {
            return Update.builder()
                    .id(id)
                    .nickname(this.nickname)
                    .isEnabled(this.isEnabled)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UpdatePassword {
        private UUID id;

        @NotBlank(message = "현재 비밀번호는 필수입니다")
        private String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수입니다")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 8-20자의 영문자, 숫자, 특수문자를 포함해야 합니다"
        )
        private String newPassword;

        @NotBlank(message = "새 비밀번호 확인은 필수입니다")
        private String newPasswordConfirm;

        public UpdatePassword withId(UUID id) {
            return UpdatePassword.builder()
                    .id(id)
                    .currentPassword(this.currentPassword)
                    .newPassword(this.newPassword)
                    .newPasswordConfirm(this.newPasswordConfirm)
                    .build();
        }
    }
}