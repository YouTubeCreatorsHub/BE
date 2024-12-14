package com.creatorhub.infrastructure.persistence.resource.mapper;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ResourceJpaMapperTest {
    @Autowired
    private ResourceJpaMapper resourceJpaMapper;

    @Test
    @DisplayName("JPA 엔티티를 도메인 엔티티로 변환할 수 있다")
    void toDomain() {
        // given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ResourceJpaEntity jpaEntity = ResourceJpaEntity.builder()
                .id(id)
                .name("테스트 리소스")
                .type(ResourceType.IMAGE)
                .url("http://example.com/image.jpg")
                .licenseType(LicenseType.FREE)
                .metadata(ResourceMetadata.createForTest())
                .createdAt(now)
                .updatedAt(now)
                .createdBy("test-user")
                .modifiedBy("test-user")
                .deleted(false)
                .build();

        // when
        ResourceEntity domainEntity = resourceJpaMapper.toDomain(jpaEntity);

        // then
        assertThat(domainEntity).isNotNull();
        assertThat(domainEntity.getId()).isEqualTo(jpaEntity.getId());
        assertThat(domainEntity.getName()).isEqualTo(jpaEntity.getName());
        assertThat(domainEntity.getType()).isEqualTo(jpaEntity.getType());
        assertThat(domainEntity.getUrl()).isEqualTo(jpaEntity.getUrl());
        assertThat(domainEntity.getLicenseType()).isEqualTo(jpaEntity.getLicenseType());
        assertThat(domainEntity.getCreatedAt()).isEqualTo(jpaEntity.getCreatedAt());
        assertThat(domainEntity.getUpdatedAt()).isEqualTo(jpaEntity.getUpdatedAt());
        assertThat(domainEntity.getCreatedBy()).isEqualTo(jpaEntity.getCreatedBy());
        assertThat(domainEntity.getModifiedBy()).isEqualTo(jpaEntity.getModifiedBy());
        assertThat(domainEntity.isDeleted()).isEqualTo(jpaEntity.isDeleted());
    }

    @Test
    @DisplayName("도메인 엔티티를 JPA 엔티티로 변환할 수 있다")
    void toJpaEntity() {
        // given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ResourceEntity domainEntity = ResourceEntity.builder()
                .id(id)
                .name("테스트 리소스")
                .type(ResourceType.IMAGE)
                .url("http://example.com/image.jpg")
                .licenseType(LicenseType.FREE)
                .metadata(ResourceMetadata.createForTest())
                .createdAt(now)
                .updatedAt(now)
                .createdBy("test-user")
                .modifiedBy("test-user")
                .deleted(false)
                .build();

        // when
        ResourceJpaEntity jpaEntity = resourceJpaMapper.toJpaEntity(domainEntity);

        // then
        assertThat(jpaEntity).isNotNull();
        assertThat(jpaEntity.getId()).isEqualTo(domainEntity.getId());
        assertThat(jpaEntity.getName()).isEqualTo(domainEntity.getName());
        assertThat(jpaEntity.getType()).isEqualTo(domainEntity.getType());
        assertThat(jpaEntity.getUrl()).isEqualTo(domainEntity.getUrl());
        assertThat(jpaEntity.getLicenseType()).isEqualTo(domainEntity.getLicenseType());
        assertThat(jpaEntity.getCreatedAt()).isEqualTo(domainEntity.getCreatedAt());
        assertThat(jpaEntity.getUpdatedAt()).isEqualTo(domainEntity.getUpdatedAt());
        assertThat(jpaEntity.getCreatedBy()).isEqualTo(domainEntity.getCreatedBy());
        assertThat(jpaEntity.getModifiedBy()).isEqualTo(domainEntity.getModifiedBy());
        assertThat(jpaEntity.isDeleted()).isEqualTo(domainEntity.isDeleted());
    }

    @Test
    @DisplayName("null 입력 시 null을 반환한다")
    void handleNullInput() {
        // when & then
        assertThat(resourceJpaMapper.toDomain(null)).isNull();
        assertThat(resourceJpaMapper.toJpaEntity(null)).isNull();
    }
}