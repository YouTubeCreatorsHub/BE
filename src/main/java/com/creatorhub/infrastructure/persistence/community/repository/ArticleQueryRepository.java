package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.ArticleSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleQueryRepository {
    Page<ArticleJpaEntity> search(ArticleSearchCondition condition, Pageable pageable);
}