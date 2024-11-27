package com.creatorhub.application.resource.port.out;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface LoadResourcePort {
    Optional<ResourceEntity> findById(UUID id);
    Page<ResourceEntity> findAll(Pageable pageable);
}