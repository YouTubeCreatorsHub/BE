package com.creatorhub.platform.member.adapter.out.persistence;

import com.creatorhub.platform.member.application.port.out.MemberPort;
import com.creatorhub.platform.member.domain.entity.Member;
import com.creatorhub.platform.member.domain.vo.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {
    private final MemberJpaRepository repository;
    private final MemberMapper mapper;

    @Override
    public Member save(Member member) {
        MemberEntity entity = mapper.toEntity(member);
        MemberEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(MemberId id) {
        repository.deleteById(id.value());
    }

    @Override
    public List<Member> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}