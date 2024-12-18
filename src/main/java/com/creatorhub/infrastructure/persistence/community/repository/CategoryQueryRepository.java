package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.CategorySearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryQueryRepository {
    Page<CategoryJpaEntity> search(CategorySearchCondition condition, Pageable pageable);
}