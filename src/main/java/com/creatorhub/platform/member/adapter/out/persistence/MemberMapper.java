package com.creatorhub.platform.member.adapter.out.persistence;

import com.creatorhub.platform.member.domain.entity.Member;
import com.creatorhub.platform.member.domain.vo.MemberId;
import com.creatorhub.platform.member.domain.vo.MemberName;
import com.creatorhub.platform.member.domain.vo.Password;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public MemberEntity toEntity(Member member) {
        return MemberEntity.builder()
                .id(member.getId().value())
                .name(member.getName().value())
                .password(member.getPassword().value())
                .status(member.getStatus())
                .build();
    }

    public Member toDomain(MemberEntity entity) {
        return Member.create(
                new MemberId(entity.getId()),
                new MemberName(entity.getName()),
                new Password(entity.getPassword())
        );
    }
}