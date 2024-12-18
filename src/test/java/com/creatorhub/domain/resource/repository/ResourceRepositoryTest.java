package com.creatorhub.domain.resource.repository;

import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import com.creatorhub.infrastructure.persistence.resource.repository.ResourceJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {"cloud.aws.s3.enabled=false"})
class ResourceRepositoryTest {
    @Autowired
    private ResourceJpaRepository resourceJpaRepository;

    @Test
    void save_ShouldPersistResource() {
        ResourceJpaEntity entity = ResourceJpaEntity.builder()
                .name("test-resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .deleted(false)
                .build();

        ResourceJpaEntity savedEntity = resourceJpaRepository.save(entity);

        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getName()).isEqualTo("test-resource");
    }


    @Test
    void findAll_ShouldReturnPagedResults() {
        ResourceJpaEntity entity1 = ResourceJpaEntity.builder()
                .name("resource1")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();

        ResourceJpaEntity entity2 = ResourceJpaEntity.builder()
                .name("resource2")
                .type(ResourceType.VIDEO)
                .licenseType(LicenseType.PREMIUM)
                .build();

        resourceJpaRepository.save(entity1);
        resourceJpaRepository.save(entity2);

        Page<ResourceJpaEntity> result = resourceJpaRepository.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchResources_ShouldApplyFilters() {
        ResourceJpaEntity entity = ResourceJpaEntity.builder()
                .name("searchable-resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();

        resourceJpaRepository.save(entity);

        // Specification을 사용하여 검색
        Specification<ResourceJpaEntity> spec = (root, query, cb) ->
                cb.equal(root.get("type"), ResourceType.IMAGE);

        Page<ResourceJpaEntity> result = resourceJpaRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo(ResourceType.IMAGE);
    }
}