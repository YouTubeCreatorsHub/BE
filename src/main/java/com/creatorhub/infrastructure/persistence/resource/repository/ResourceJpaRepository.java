package com.creatorhub.infrastructure.persistence.resource.repository;

import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ResourceJpaRepository extends JpaRepository<ResourceJpaEntity, UUID>,
        JpaSpecificationExecutor<ResourceJpaEntity> {

}