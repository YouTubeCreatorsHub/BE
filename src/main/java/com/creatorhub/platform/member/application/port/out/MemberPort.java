package com.creatorhub.platform.member.application.port.out;

import com.creatorhub.platform.member.domain.entity.Member;
import com.creatorhub.platform.member.domain.vo.MemberId;

import java.util.List;
import java.util.Optional;

public interface MemberPort {
    Member save(Member member);
    Optional<Member> findById(MemberId id);
    void deleteById(MemberId id);
    List<Member> findAll();
}

