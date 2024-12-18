package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.MemberSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.MemberJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberQueryRepository {
    Page<MemberJpaEntity> search(MemberSearchCondition condition, Pageable pageable);
}