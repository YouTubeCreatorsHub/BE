package com.creatorhub.platform.member.application.service;

import com.creatorhub.platform.member.application.port.out.MemberPort;
import com.creatorhub.platform.member.application.port.in.CreateMemberCommand;
import com.creatorhub.platform.member.application.port.in.CreateMemberUseCase;
import com.creatorhub.platform.member.common.MemberNotFoundException;
import com.creatorhub.platform.member.domain.entity.Member;
import com.creatorhub.platform.member.domain.vo.MemberId;
import com.creatorhub.platform.member.domain.vo.MemberName;
import com.creatorhub.platform.member.domain.vo.Password;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements CreateMemberUseCase {
    private final MemberPort memberPort;

    @Override
    public Member createMember(CreateMemberCommand command) {
        Member member = Member.create(
                new MemberId(command.id()),
                new MemberName(command.name()),
                new Password(command.password())
        );

        return memberPort.save(member);
    }

    public Member getMember(String id) {
        return memberPort.findById(new MemberId(id))
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    public List<Member> getAllMembers() {
        return memberPort.findAll();
    }

    public void deleteMember(String id) {
        memberPort.deleteById(new MemberId(id));
    }
}