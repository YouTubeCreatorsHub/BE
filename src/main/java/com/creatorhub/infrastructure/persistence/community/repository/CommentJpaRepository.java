package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.infrastructure.persistence.community.entity.CommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, UUID>,
        JpaSpecificationExecutor<CommentJpaEntity> {

    @Query("SELECT c FROM CommentJpaEntity c WHERE c.article.id = :articleId AND c.deleted = false")
    List<CommentJpaEntity> findAllByArticleId(@Param("articleId") UUID articleId);

    @Query("SELECT COUNT(c) FROM CommentJpaEntity c WHERE c.article.id = :articleId AND c.deleted = false")
    long countByArticleId(@Param("articleId") UUID articleId);
}