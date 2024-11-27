package com.creatorhub.infrastructure.persistence.resource.adapter;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.repository.ResourceRepository;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceJpaMapper;
import com.creatorhub.infrastructure.persistence.resource.repository.ResourceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResourcePersistenceAdapter implements ResourceRepository {
    private final ResourceJpaRepository resourceJpaRepository;
    private final ResourceJpaMapper resourceJpaMapper;

    @Override
    public ResourceEntity save(ResourceEntity resource) {
        return resourceJpaMapper.toDomain(
                resourceJpaRepository.save(
                        resourceJpaMapper.toJpaEntity(resource)
                )
        );
    }

    @Override
    public Optional<ResourceEntity> findById(UUID id) {
        return resourceJpaRepository.findById(id)
                .map(resourceJpaMapper::toDomain);
    }

    @Override
    public Page<ResourceEntity> findAll(Pageable pageable) {
        return resourceJpaRepository.findAll(pageable)
                .map(resourceJpaMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        resourceJpaRepository.deleteById(id);
    }
}