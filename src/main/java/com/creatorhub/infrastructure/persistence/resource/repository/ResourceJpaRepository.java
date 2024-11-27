package com.creatorhub.infrastructure.persistence.resource.repository;

import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ResourceJpaRepository extends JpaRepository<ResourceJpaEntity, UUID> {
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 정의
}
