package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.infrastructure.persistence.community.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, UUID>,
        JpaSpecificationExecutor<MemberJpaEntity> {

    @Query("SELECT m FROM MemberJpaEntity m WHERE m.email = :email AND m.deleted = false")
    Optional<MemberJpaEntity> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}