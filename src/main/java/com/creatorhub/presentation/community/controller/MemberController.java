package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.community.member.dto.MemberResponse;
import com.creatorhub.application.community.member.port.in.GetMemberUseCase;
import com.creatorhub.application.community.member.port.in.ManageMemberUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "회원", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final ManageMemberUseCase manageMemberUseCase;
    private final GetMemberUseCase getMemberUseCase;

    @Operation(
            summary = "회원 가입",
            description = "새로운 회원을 등록합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원 가입 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이메일 중복, 비밀번호 형식 오류 등)"
            )
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> signup(
            @Valid @RequestBody MemberCommand.Create command) {
        return ApiResponse.success(manageMemberUseCase.createMember(command));
    }

    @Operation(
            summary = "회원 정보 조회",
            description = "회원 ID로 정보를 조회합니다. 관리자만 접근 가능합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MemberResponse> getMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable UUID id) {
        return ApiResponse.success(getMemberUseCase.getMember(id));
    }

    @Operation(
            summary = "이메일로 회원 정보 조회",
            description = "이메일로 회원 정보를 조회합니다. 관리자만 접근 가능합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MemberResponse> getMemberByEmail(
            @Parameter(description = "이메일", required = true)
            @PathVariable String email) {
        return ApiResponse.success(getMemberUseCase.getMemberByEmail(email));
    }

    @Operation(
            summary = "회원 목록 조회",
            description = "회원 목록을 페이징하여 조회합니다. 관리자만 접근 가능합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @Parameters({
            @Parameter(
                    name = "page",
                    description = "페이지 번호 (0부터 시작)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "0")
            ),
            @Parameter(
                    name = "size",
                    description = "페이지 크기",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "20")
            ),
            @Parameter(
                    name = "sort",
                    description = "정렬 기준 (예: createdAt,desc)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "string")
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<MemberResponse>> getAllMembers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(getMemberUseCase.getAllMembers(pageable));
    }

    @Operation(
            summary = "회원 정보 수정",
            description = "회원 정보를 수정합니다. 본인 정보만 수정 가능합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음"
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (authentication.principal.username == @memberService.getMemberEmail(#id) or hasRole('ADMIN'))")
    public ApiResponse<MemberResponse> updateMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody MemberCommand.Update command,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(manageMemberUseCase.updateMember(command.withId(id)));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "회원의 비밀번호를 변경합니다. 본인만 변경 가능합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated() and authentication.principal.username == @memberService.getMemberEmail(#id)")
    public ApiResponse<MemberResponse> updatePassword(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody MemberCommand.UpdatePassword command,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(manageMemberUseCase.updatePassword(command.withId(id)));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "회원을 탈퇴처리합니다. 본인 또는 관리자만 가능합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated() and (authentication.principal.username == @memberService.getMemberEmail(#id) or hasRole('ADMIN'))")
    public ApiResponse<Void> deleteMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        manageMemberUseCase.deleteMember(id);
        return ApiResponse.success();
    }

    @Operation(
            summary = "이메일 중복 체크",
            description = "회원 가입 시 이메일 중복 여부를 체크합니다."
    )
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmailDuplicate(
            @Parameter(description = "체크할 이메일", required = true)
            @RequestParam String email) {
        return ApiResponse.success(getMemberUseCase.existsByEmail(email));
    }

    @Operation(
            summary = "닉네임 중복 체크",
            description = "회원 가입 시 닉네임 중복 여부를 체크합니다."
    )
    @GetMapping("/check-nickname")
    public ApiResponse<Boolean> checkNicknameDuplicate(
            @Parameter(description = "체크할 닉네임", required = true)
            @RequestParam String nickname) {
        return ApiResponse.success(getMemberUseCase.existsByNickname(nickname));
    }
}