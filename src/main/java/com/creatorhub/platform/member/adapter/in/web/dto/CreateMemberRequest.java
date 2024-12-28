package com.creatorhub.platform.member.adapter.in.web.dto;

import com.creatorhub.platform.member.application.port.in.CreateMemberCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateMemberRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String id;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자리이면서 알파벳, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    public CreateMemberCommand toCommand() {
        return new CreateMemberCommand(
                this.id,
                this.name,
                this.password
        );
    }
}