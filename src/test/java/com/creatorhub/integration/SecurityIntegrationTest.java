package com.creatorhub.integration;

import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.community.member.dto.MemberResponse;
import com.creatorhub.application.community.member.service.MemberService;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.infrastructure.storage.s3.service.image.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@Import(TestSecurityConfig.class)
class SecurityIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTestSupport jwtTestSupport;
    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("인증되지 않은 사용자는 보호된 리소스에 접근할 수 없다")
    @WithAnonymousUser
    void accessProtectedResource_WithoutAuthentication_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER 권한을 가진 사용자는 관리자 리소스에 접근할 수 없다")
    @WithMockUser(roles = "USER")
    void accessAdminResource_WithUserRole_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN 권한을 가진 사용자는 관리자 리소스에 접근할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void accessAdminResource_WithAdminRole_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입은 인증없이 가능하다")
    @WithAnonymousUser
    void signUp_WithoutAuthentication_ShouldSucceed() throws Exception {
        // given
        MemberCommand.Create command = MemberCommand.Create.builder()
                .email("test@example.com")
                .password("Test1234!")
                .nickname("tester")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("본인의 회원 정보만 수정할 수 있다")
    @WithMockUser(username = "test@example.com")
    void updateMemberInfo_OnlyOwnInfo_ShouldSucceed() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.Update command = MemberCommand.Update.builder()
                .nickname("newNickname")
                .build();

        given(memberService.getMemberEmail(memberId)).willReturn("test@example.com");
        given(memberService.updateMember(any(MemberCommand.Update.class)))
                .willReturn(MemberResponse.builder()
                        .id(memberId)
                        .email("test@example.com")
                        .nickname("newNickname")
                        .build());

        // when & then
        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("다른 사용자의 회원 정보는 수정할 수 없다")
    @WithMockUser(username = "other@example.com")
    void updateMemberInfo_OtherUserInfo_ShouldFail() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.Update command = MemberCommand.Update.builder()
                .nickname("newNickname")
                .build();

        given(memberService.getMemberEmail(memberId)).willReturn("test@example.com");
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(memberService)
                .updateMember(any(MemberCommand.Update.class));

        // when & then
        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("관리자는 모든 회원의 정보를 수정할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void updateMemberInfo_AsAdmin_ShouldSucceed() throws Exception {
        // given
        MemberCommand.Update command = MemberCommand.Update.builder()
                .nickname("newNickname")
                .isEnabled(false)
                .build();

        UUID memberId = UUID.randomUUID(); // 실제로는 테스트용 회원 ID를 사용

        // when & then
        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JWT 토큰 없이 보호된 리소스에 접근할 수 없다")
    void accessProtectedResource_WithoutToken_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/v1/members/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 JWT 토큰으로 보호된 리소스에 접근할 수 없다")
    void accessProtectedResource_WithInvalidToken_ShouldFail() throws Exception {
        String invalidToken = "Bearer invalid.token.here";

        mockMvc.perform(get("/api/v1/members/me")
                        .header("Authorization", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("만료된 JWT 토큰으로 보호된 리소스에 접근할 수 없다")
    void accessProtectedResource_WithExpiredToken_ShouldFail() throws Exception {
        String expiredToken = "Bearer " + generateExpiredToken();

        mockMvc.perform(get("/api/v1/members/me")
                        .header("Authorization", expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private String generateExpiredToken() {
        // 실제 구현에서는 JWT 유틸리티를 사용하여 만료된 토큰을 생성
        return jwtTestSupport.generateExpiredToken("test@example.com");
    }
}