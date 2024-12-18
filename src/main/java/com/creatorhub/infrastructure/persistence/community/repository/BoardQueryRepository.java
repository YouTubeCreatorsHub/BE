package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.BoardSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardQueryRepository {
    Page<BoardJpaEntity> search(BoardSearchCondition condition, Pageable pageable);
}