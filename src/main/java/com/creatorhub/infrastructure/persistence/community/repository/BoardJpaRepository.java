package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BoardJpaRepository extends JpaRepository<BoardJpaEntity, UUID>,
        JpaSpecificationExecutor<BoardJpaEntity> {

    boolean existsByName(String name);

    @Query("SELECT b FROM BoardJpaEntity b WHERE b.isEnabled = true AND b.deleted = false")
    List<BoardJpaEntity> findAllEnabled();
}