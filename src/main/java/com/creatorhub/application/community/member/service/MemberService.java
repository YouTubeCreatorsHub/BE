package com.creatorhub.application.community.member.service;

import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.community.member.dto.MemberResponse;
import com.creatorhub.application.community.member.exception.DuplicateMemberException;
import com.creatorhub.application.community.member.exception.MemberNotFoundException;
import com.creatorhub.application.community.member.exception.PasswordMismatchException;
import com.creatorhub.application.community.member.port.in.GetMemberUseCase;
import com.creatorhub.application.community.member.port.in.ManageMemberUseCase;
import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements ManageMemberUseCase, GetMemberUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponse createMember(MemberCommand.Create command) {
        validateNewMember(command);

        Member member = Member.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .nickname(command.getNickname())
                .role(Member.Role.USER)
                .isEnabled(command.isEnabled())
                .build();

        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(MemberCommand.Update command) {
        Member member = memberRepository.findById(command.getId())
                .orElseThrow(() -> new MemberNotFoundException(command.getId()));

        if (command.getNickname() != null &&
                !command.getNickname().equals(member.getNickname()) &&
                memberRepository.existsByNickname(command.getNickname())) {
            throw new DuplicateMemberException("닉네임", command.getNickname());
        }

        if (command.getNickname() != null) {
            member.updateNickname(command.getNickname());
        }
        if (command.getIsEnabled() != null) {
            member.setEnabled(command.getIsEnabled());
        }

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updatePassword(MemberCommand.UpdatePassword command) {
        Member member = memberRepository.findById(command.getId())
                .orElseThrow(() -> new MemberNotFoundException(command.getId()));

        if (!passwordEncoder.matches(command.getCurrentPassword(), member.getPassword())) {
            throw new PasswordMismatchException();
        }

        if (!command.getNewPassword().equals(command.getNewPasswordConfirm())) {
            throw new PasswordMismatchException("새 비밀번호가 일치하지 않습니다.");
        }

        // 암호화 전에 비밀번호 유효성 검증
        member.validateNewPassword(command.getNewPassword());

        // 검증 통과 후 암호화하여 저장
        member.setPassword(passwordEncoder.encode(command.getNewPassword()));
        member = memberRepository.save(member);

        return MemberResponse.from(member);
    }
    @Override
    @Transactional
    public void deleteMember(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        // 실제 삭제 대신 soft delete 수행
        member.setEnabled(false);
        memberRepository.save(member);
    }

    @Override
    public MemberResponse getMember(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        return MemberResponse.from(member);
    }

    @Override
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
        return MemberResponse.from(member);
    }

    @Override
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberResponse::from);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    private void validateNewMember(MemberCommand.Create command) {
        if (memberRepository.existsByEmail(command.getEmail())) {
            throw new DuplicateMemberException("이메일", command.getEmail());
        }
        if (memberRepository.existsByNickname(command.getNickname())) {
            throw new DuplicateMemberException("닉네임", command.getNickname());
        }
    }
}