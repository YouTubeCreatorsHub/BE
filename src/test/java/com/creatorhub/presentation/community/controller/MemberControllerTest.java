package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.community.member.dto.MemberResponse;
import com.creatorhub.application.community.member.exception.DuplicateMemberException;
import com.creatorhub.application.community.member.exception.PasswordMismatchException;
import com.creatorhub.application.community.member.service.MemberService;
import com.creatorhub.domain.community.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입을 할 수 있다")
    void signup() throws Exception {
        // given
        MemberCommand.Create command = MemberCommand.Create.builder()
                .email("test@test.com")
                .password("Password123!")
                .nickname("테스터")
                .build();

        MemberResponse response = MemberResponse.builder()
                .id(UUID.randomUUID())
                .email(command.getEmail())
                .nickname(command.getNickname())
                .role(Member.Role.USER)
                .isEnabled(true)
                .build();

        given(memberService.createMember(any(MemberCommand.Create.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/members/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(command.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(command.getNickname()));
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입하면 400 응답을 반환한다")
    void signupWithDuplicateEmail() throws Exception {
        // given
        MemberCommand.Create command = MemberCommand.Create.builder()
                .email("duplicate@test.com")
                .password("Password123!")
                .nickname("테스터")
                .build();

        given(memberService.createMember(any(MemberCommand.Create.class)))
                .willThrow(new DuplicateMemberException("이메일", command.getEmail()));

        // when & then
        mockMvc.perform(post("/api/v1/members/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("회원 정보를 조회할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void getMember() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        MemberResponse response = MemberResponse.builder()
                .id(memberId)
                .email("test@test.com")
                .nickname("테스터")
                .role(Member.Role.USER)
                .isEnabled(true)
                .build();

        given(memberService.getMember(memberId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/members/{id}", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(memberId.toString()));
    }

    @Test
    @DisplayName("이메일로 회원 정보를 조회할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void getMemberByEmail() throws Exception {
        // given
        String email = "test@test.com";
        MemberResponse response = MemberResponse.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickname("테스터")
                .role(Member.Role.USER)
                .isEnabled(true)
                .build();

        given(memberService.getMemberByEmail(email)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/members/by-email/{email}", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(email));
    }

    @Test
    @DisplayName("관리자는 전체 회원 목록을 조회할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void getAllMembers() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<MemberResponse> members = List.of(
                MemberResponse.builder()
                        .id(UUID.randomUUID())
                        .email("test1@test.com")
                        .nickname("테스터1")
                        .role(Member.Role.USER)
                        .isEnabled(true)
                        .build(),
                MemberResponse.builder()
                        .id(UUID.randomUUID())
                        .email("test2@test.com")
                        .nickname("테스터2")
                        .role(Member.Role.USER)
                        .isEnabled(true)
                        .build()
        );
        Page<MemberResponse> memberPage = new PageImpl<>(members, pageRequest, members.size());

        given(memberService.getAllMembers(any(PageRequest.class))).willReturn(memberPage);

        // when & then
        mockMvc.perform(get("/api/v1/members")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("회원 정보를 수정할 수 있다")
    @WithMockUser(username = "test@test.com")  // 실제 이메일과 일치하도록 설정
    void updateMember() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.Update command = MemberCommand.Update.builder()
                .id(memberId)
                .nickname("수정된닉네임")
                .isEnabled(true)
                .build();

        MemberResponse response = MemberResponse.builder()
                .id(memberId)
                .email("test@test.com")
                .nickname(command.getNickname())
                .role(Member.Role.USER)
                .isEnabled(command.getIsEnabled())
                .build();

        given(memberService.getMemberEmail(memberId)).willReturn("test@test.com");  // mock 추가
        given(memberService.updateMember(any(MemberCommand.Update.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value(command.getNickname()));
    }

    @Test
    @DisplayName("비밀번호를 변경할 수 있다")
    @WithMockUser(username = "test@test.com")
    void updatePassword() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.UpdatePassword command = MemberCommand.UpdatePassword.builder()
                .id(memberId)
                .currentPassword("OldPassword123!")
                .newPassword("NewPassword123!")
                .newPasswordConfirm("NewPassword123!")
                .build();

        MemberResponse response = MemberResponse.builder()  // response 객체 생성
                .id(memberId)
                .email("test@test.com")
                .nickname("테스터")
                .role(Member.Role.USER)
                .isEnabled(true)
                .build();

        given(memberService.getMemberEmail(memberId)).willReturn("test@test.com");
        given(memberService.updatePassword(any(MemberCommand.UpdatePassword.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/members/{id}/password", memberId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하지 않으면 비밀번호 변경이 실패한다")
    @WithMockUser(username = "test@test.com")  // 테스트 사용자 이메일 설정
    void updatePasswordWithWrongCurrentPassword() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.UpdatePassword command = MemberCommand.UpdatePassword.builder()
                .id(memberId)
                .currentPassword("WrongPassword123!")
                .newPassword("NewPassword123!")
                .newPasswordConfirm("NewPassword123!")
                .build();

        given(memberService.getMemberEmail(memberId)).willReturn("test@test.com");  // 권한 검증을 위한 mock
        given(memberService.updatePassword(any(MemberCommand.UpdatePassword.class)))
                .willThrow(new PasswordMismatchException());

        // when & then
        mockMvc.perform(put("/api/v1/members/{id}/password", memberId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("회원을 삭제할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void deleteMember() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/v1/members/{id}", memberId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}