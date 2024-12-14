package com.creatorhub.domain.community.repository;

import com.creatorhub.domain.community.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(UUID id);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    void deleteById(UUID id);
    Page<Member> findAll(Pageable pageable);
}