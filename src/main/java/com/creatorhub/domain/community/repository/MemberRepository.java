package com.creatorhub.domain.community.repository;

import com.creatorhub.domain.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
