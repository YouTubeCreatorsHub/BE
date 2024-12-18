package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.ResourceSearchCriteria;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.repository.ResourceRepository;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
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

import java.time.LocalDateTime;
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

    @Test
    void updateResource_WithValidCommand_ShouldUpdateResource() {
        // given
        UUID id = UUID.randomUUID();
        UpdateResourceCommand command = UpdateResourceCommand.builder()
                .name("updated-name")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();
        ResourceEntity entity = mock(ResourceEntity.class);
        ResourceResponse response = mock(ResourceResponse.class);

        when(resourceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(resourceRepository.save(entity)).thenReturn(entity);
        when(resourceMapper.toResponse(entity)).thenReturn(response);

        // when
        ResourceResponse result = resourceService.updateResource(id, command);

        // then
        assertThat(result).isNotNull();
        verify(entity).update(command.getName(), command.getType(), command.getLicenseType());
    }

    @Test
    void updateResource_WithInvalidId_ShouldThrowException() {
        // given
        UUID invalidId = UUID.randomUUID();
        UpdateResourceCommand command = UpdateResourceCommand.builder()
                .name("test")
                .build();

        when(resourceRepository.findById(invalidId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> resourceService.updateResource(invalidId, command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void updateResource_WithInvalidData_ShouldThrowException() {
        // given
        UUID id = UUID.randomUUID();
        UpdateResourceCommand invalidCommand = UpdateResourceCommand.builder()
                .name("")  // 빈 이름
                .build();
        ResourceEntity entity = mock(ResourceEntity.class);

        when(resourceRepository.findById(id)).thenReturn(Optional.of(entity));
        doThrow(new BusinessException(ErrorCode.INVALID_INPUT))
                .when(entity).update(any(), any(), any());

        // then
        assertThatThrownBy(() -> resourceService.updateResource(id, invalidCommand))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }

    @Test
    void searchResources_WithValidCriteria_ShouldReturnMatchingResources() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .name("test")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .minSize(1000L)
                .maxSize(5000L)
                .fromDate(LocalDateTime.now().minusDays(7))
                .toDate(LocalDateTime.now())
                .formatType("image/jpeg")
                .build();

        Pageable pageable = mock(Pageable.class);
        ResourceEntity entity = mock(ResourceEntity.class);
        ResourceResponse response = mock(ResourceResponse.class);
        Page<ResourceEntity> page = new PageImpl<>(List.of(entity));

        when(resourceRepository.searchResources(eq(criteria), eq(pageable))).thenReturn(page);
        when(resourceMapper.toResponse(entity)).thenReturn(response);

        // when
        Page<ResourceResponse> result = resourceService.searchResources(criteria, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(resourceRepository).searchResources(eq(criteria), eq(pageable));
    }

    @Test
    void searchResources_WithMultipleFilters_ShouldReturnFilteredResults() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .minSize(1000L)
                .build();

        Pageable pageable = mock(Pageable.class);
        ResourceEntity entity1 = mock(ResourceEntity.class);
        ResourceEntity entity2 = mock(ResourceEntity.class);
        ResourceResponse response1 = mock(ResourceResponse.class);
        ResourceResponse response2 = mock(ResourceResponse.class);
        Page<ResourceEntity> page = new PageImpl<>(List.of(entity1, entity2));

        when(resourceRepository.searchResources(eq(criteria), eq(pageable))).thenReturn(page);
        when(resourceMapper.toResponse(entity1)).thenReturn(response1);
        when(resourceMapper.toResponse(entity2)).thenReturn(response2);

        // when
        Page<ResourceResponse> result = resourceService.searchResources(criteria, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(resourceRepository).searchResources(eq(criteria), eq(pageable));
    }

    @Test
    void searchResources_WithInvalidCriteria_ShouldReturnEmptyResults() {
        // given
        ResourceSearchCriteria invalidCriteria = ResourceSearchCriteria.builder()
                .name("")
                .minSize(-1L)
                .build();

        Pageable pageable = mock(Pageable.class);
        Page<ResourceEntity> emptyPage = new PageImpl<>(List.of());

        when(resourceRepository.searchResources(eq(invalidCriteria), eq(pageable))).thenReturn(emptyPage);

        // when
        Page<ResourceResponse> result = resourceService.searchResources(invalidCriteria, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(resourceRepository).searchResources(eq(invalidCriteria), eq(pageable));
    }
}