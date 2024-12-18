package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID>,
        JpaSpecificationExecutor<CategoryJpaEntity> {

    @Query("SELECT c FROM CategoryJpaEntity c WHERE c.board.id = :boardId AND c.deleted = false")
    List<CategoryJpaEntity> findAllByBoardId(@Param("boardId") UUID boardId);

    boolean existsByNameAndBoardId(String name, UUID boardId);
}