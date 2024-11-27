package com.creatorhub.domain.resource.repository;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository {
    ResourceEntity save(ResourceEntity resource);

    Optional<ResourceEntity> findById(UUID id);

    Page<ResourceEntity> findAll(Pageable pageable);

    void deleteById(UUID id);
}