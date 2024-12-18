package com.creatorhub.application.community.member.service;

import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.community.member.dto.MemberResponse;
import com.creatorhub.application.community.member.exception.DuplicateMemberException;
import com.creatorhub.application.community.member.exception.PasswordMismatchException;
import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원을 생성할 수 있다")
    void createMember() {
        // given
        MemberCommand.Create command = MemberCommand.Create.builder()
                .email("test@test.com")
                .password("password123!")
                .nickname("테스터")
                .build();

        Member member = Member.builder()
                .articles(new ArrayList<>())
                .comments(new ArrayList<>())
                .email(command.getEmail())
                .password("encoded_password")
                .nickname(command.getNickname())
                .role(Member.Role.USER)
                .isEnabled(true)
                .build();

        given(memberRepository.existsByEmail(command.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(command.getNickname())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encoded_password");
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        MemberResponse response = memberService.createMember(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(command.getEmail());
        assertThat(response.getNickname()).isEqualTo(command.getNickname());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("중복된 이메일로 회원 가입하면 예외가 발생한다")
    void createMemberWithDuplicateEmail() {
        // given
        MemberCommand.Create command = MemberCommand.Create.builder()
                .email("duplicate@test.com")
                .password("password123!")
                .nickname("테스터")
                .build();

        given(memberRepository.existsByEmail(command.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.createMember(command))
                .isInstanceOf(DuplicateMemberException.class);
    }

    @Test
    @DisplayName("회원 정보를 수정할 수 있다")
    void updateMember() {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.Update command = MemberCommand.Update.builder()
                .id(memberId)
                .nickname("수정된닉네임")
                .isEnabled(false)
                .build();

        Member member = Member.builder()
                .id(memberId)
                .articles(new ArrayList<>())
                .comments(new ArrayList<>())
                .email("test@test.com")
                .password("encoded_password")
                .nickname("원래닉네임")
                .isEnabled(true)
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname(command.getNickname())).willReturn(false);

        // when
        MemberResponse response = memberService.updateMember(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo(command.getNickname());
        assertThat(response.isEnabled()).isEqualTo(command.getIsEnabled());
    }

    @Test
    @DisplayName("비밀번호를 변경할 수 있다")
    void updatePassword() {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.UpdatePassword command = MemberCommand.UpdatePassword.builder()
                .id(memberId)
                .currentPassword("OldPass1!")
                .newPassword("NewPass2@")
                .newPasswordConfirm("NewPass2@")
                .build();

        Member member = Member.builder()
                .id(memberId)
                .articles(new ArrayList<>())
                .comments(new ArrayList<>())
                .email("test@test.com")
                .password("encoded_old_password")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(command.getCurrentPassword(), member.getPassword())).willReturn(true);
        given(passwordEncoder.encode(command.getNewPassword())).willReturn("encoded_new_password");
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        MemberResponse response = memberService.updatePassword(command);

        // then
        assertThat(response).isNotNull();
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하지 않으면 비밀번호 변경 시 예외가 발생한다")
    void updatePasswordWithWrongCurrentPassword() {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.UpdatePassword command = MemberCommand.UpdatePassword.builder()
                .id(memberId)
                .currentPassword("wrongPassword123!")
                .newPassword("newPassword123!")
                .newPasswordConfirm("newPassword123!")
                .build();

        Member member = Member.builder()
                .id(memberId)
                .email("test@test.com")
                .password("encoded_old_password")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(command.getCurrentPassword(), member.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.updatePassword(command))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    @DisplayName("새 비밀번호와 확인이 일치하지 않으면 예외가 발생한다")
    void updatePasswordWithMismatchedNewPassword() {
        // given
        UUID memberId = UUID.randomUUID();
        MemberCommand.UpdatePassword command = MemberCommand.UpdatePassword.builder()
                .id(memberId)
                .currentPassword("oldPassword123!")
                .newPassword("newPassword123!")
                .newPasswordConfirm("differentPassword123!")
                .build();

        Member member = Member.builder()
                .id(memberId)
                .email("test@test.com")
                .password("encoded_old_password")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(command.getCurrentPassword(), member.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.updatePassword(command))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessage("새 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있다")
    void getMemberByEmail() {
        // given
        String email = "test@test.com";
        Member member = Member.builder()
                .articles(new ArrayList<>())
                .comments(new ArrayList<>())
                .email(email)
                .nickname("테스터")
                .build();

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.getMemberByEmail(email);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("전체 회원을 페이징하여 조회할 수 있다")
    void getAllMembers() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Member> members = List.of(
                Member.builder()
                        .articles(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .email("test1@test.com")
                        .nickname("테스터1")
                        .build(),
                Member.builder()
                        .articles(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .email("test2@test.com")
                        .nickname("테스터2")
                        .build()
        );
        Page<Member> memberPage = new PageImpl<>(members, pageRequest, members.size());

        given(memberRepository.findAll(pageRequest)).willReturn(memberPage);

        // when
        Page<MemberResponse> responses = memberService.getAllMembers(pageRequest);

        // then
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("회원을 삭제(비활성화)할 수 있다")
    void deleteMember() {
        // given
        UUID memberId = UUID.randomUUID();
        Member member = Member.builder()
                .id(memberId)
                .email("test@test.com")
                .isEnabled(true)
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        memberService.deleteMember(memberId);

        // then
        assertThat(member.isEnabled()).isFalse();
        verify(memberRepository).save(member);
    }
}