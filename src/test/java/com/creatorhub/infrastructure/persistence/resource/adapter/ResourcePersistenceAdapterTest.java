package com.creatorhub.infrastructure.persistence.resource.adapter;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceJpaMapper;
import com.creatorhub.infrastructure.persistence.resource.repository.ResourceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourcePersistenceAdapterTest {

    @Mock
    private ResourceJpaRepository resourceJpaRepository;

    @Mock
    private ResourceJpaMapper resourceJpaMapper;

    private ResourcePersistenceAdapter resourcePersistenceAdapter;

    @BeforeEach
    void setUp() {
        resourcePersistenceAdapter = new ResourcePersistenceAdapter(
                resourceJpaRepository,
                resourceJpaMapper
        );
    }

    @Test
    void save_Success() {
        // Given
        ResourceEntity domainEntity = ResourceEntity.builder()
                .name("Test Resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();

        ResourceJpaEntity jpaEntity = mock(ResourceJpaEntity.class);
        ResourceJpaEntity savedJpaEntity = mock(ResourceJpaEntity.class);

        when(resourceJpaMapper.toJpaEntity(domainEntity)).thenReturn(jpaEntity);
        when(resourceJpaRepository.save(jpaEntity)).thenReturn(savedJpaEntity);
        when(resourceJpaMapper.toDomain(savedJpaEntity)).thenReturn(domainEntity);

        // When
        ResourceEntity result = resourcePersistenceAdapter.save(domainEntity);

        // Then
        assertThat(result).isNotNull();
        verify(resourceJpaRepository).save(any(ResourceJpaEntity.class));
    }

    @Test
    void findById_Success() {
        // Given
        UUID id = UUID.randomUUID();
        ResourceJpaEntity jpaEntity = mock(ResourceJpaEntity.class);
        ResourceEntity domainEntity = ResourceEntity.builder()
                .name("Test Resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();

        when(resourceJpaRepository.findById(id)).thenReturn(Optional.of(jpaEntity));
        when(resourceJpaMapper.toDomain(jpaEntity)).thenReturn(domainEntity);

        // When
        Optional<ResourceEntity> result = resourcePersistenceAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Resource");
        verify(resourceJpaRepository).findById(id);
    }
}