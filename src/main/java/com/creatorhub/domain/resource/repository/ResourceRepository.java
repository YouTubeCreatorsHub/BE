package com.creatorhub.domain.resource.repository;

import com.creatorhub.application.resource.dto.ResourceSearchCriteria;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository {  // JpaRepository 상속 제거
    ResourceEntity save(ResourceEntity resource);
    Optional<ResourceEntity> findById(UUID id);
    Page<ResourceEntity> findAll(Pageable pageable);
    void deleteById(UUID id);
    Page<ResourceEntity> searchResources(ResourceSearchCriteria criteria, Pageable pageable);
}