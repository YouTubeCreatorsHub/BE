package com.creatorhub.platform.community.repository;

import com.creatorhub.platform.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
