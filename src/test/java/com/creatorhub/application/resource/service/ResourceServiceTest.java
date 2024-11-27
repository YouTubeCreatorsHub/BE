package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.repository.ResourceRepository;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    void createResource_Success() {
        // Given
        CreateResourceCommand command = mock(CreateResourceCommand.class);
        ResourceEntity entity = mock(ResourceEntity.class);
        ResourceResponse response = mock(ResourceResponse.class);

        when(resourceMapper.toEntity(command)).thenReturn(entity);
        when(resourceRepository.save(entity)).thenReturn(entity);
        when(resourceMapper.toResponse(entity)).thenReturn(response);

        // When
        ResourceResponse result = resourceService.createResource(command);

        // Then
        assertThat(result).isNotNull();
        verify(resourceRepository).save(any(ResourceEntity.class));
    }

    @Test
    void getResource_Success() {
        // Given
        UUID id = UUID.randomUUID();
        ResourceEntity entity = mock(ResourceEntity.class);
        ResourceResponse response = mock(ResourceResponse.class);

        when(resourceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(resourceMapper.toResponse(entity)).thenReturn(response);

        // When
        ResourceResponse result = resourceService.getResource(id);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getAllResources_Success() {
        // Given
        Pageable pageable = mock(Pageable.class);
        ResourceEntity entity = mock(ResourceEntity.class);
        ResourceResponse response = mock(ResourceResponse.class);
        Page<ResourceEntity> page = new PageImpl<>(List.of(entity));

        when(resourceRepository.findAll(pageable)).thenReturn(page);
        when(resourceMapper.toResponse(entity)).thenReturn(response);

        // When
        Page<ResourceResponse> result = resourceService.getAllResources(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getResource_NotFound_ThrowsException() {
        // Given
        UUID id = UUID.randomUUID();
        when(resourceRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> resourceService.getResource(id))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
    }
}