package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.application.resource.port.in.CreateResourceUseCase;
import com.creatorhub.application.resource.port.in.DeleteResourceUseCase;
import com.creatorhub.application.resource.port.in.GetResourceUseCase;
import com.creatorhub.application.resource.port.in.UpdateResourceUseCase;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.repository.ResourceRepository;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceService implements CreateResourceUseCase, GetResourceUseCase,
        UpdateResourceUseCase, DeleteResourceUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Override
    @Transactional
    public ResourceResponse createResource(CreateResourceCommand command) {
        ResourceEntity entity = resourceMapper.toEntity(command);
        ResourceEntity savedEntity = resourceRepository.save(entity);
        return resourceMapper.toResponse(savedEntity);
    }

    @Override
    public ResourceResponse getResource(UUID id) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        return resourceMapper.toResponse(entity);
    }

    @Override
    public Page<ResourceResponse> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(resourceMapper::toResponse);
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(UUID id, UpdateResourceCommand command) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        entity.update(command.getName(), command.getType(), command.getLicenseType());
        return resourceMapper.toResponse(resourceRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteResource(UUID id) {
        resourceRepository.deleteById(id);
    }
}