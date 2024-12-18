package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.CommentSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.CommentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentQueryRepository {
    Page<CommentJpaEntity> search(CommentSearchCondition condition, Pageable pageable);
}