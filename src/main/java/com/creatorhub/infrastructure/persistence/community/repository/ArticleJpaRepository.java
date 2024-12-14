package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ArticleJpaRepository extends JpaRepository<ArticleJpaEntity, UUID>,
        JpaSpecificationExecutor<ArticleJpaEntity> {

    @Query("SELECT a FROM ArticleJpaEntity a WHERE a.board.id = :boardId AND a.deleted = false")
    List<ArticleJpaEntity> findAllByBoardId(@Param("boardId") UUID boardId);

    @Query("SELECT COUNT(a) FROM ArticleJpaEntity a WHERE a.board.id = :boardId AND a.deleted = false")
    long countByBoardId(@Param("boardId") UUID boardId);
}