package com.creatorhub.infrastructure.persistence.resource.specification;

import com.creatorhub.application.resource.dto.ResourceSearchCriteria;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceSpecificationTest {

    @Test
    void withName_ShouldCreateValidSpecification() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .name("test")
                .build();

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }

    @Test
    void withType_ShouldCreateValidSpecification() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .type(ResourceType.IMAGE)
                .build();

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }

    @Test
    void withLicenseType_ShouldCreateValidSpecification() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .licenseType(LicenseType.FREE)
                .build();

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }

    @Test
    void withSizeRange_ShouldCreateValidSpecification() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .minSize(1000L)
                .maxSize(5000L)
                .build();

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }

    @Test
    void withDateRange_ShouldCreateValidSpecification() {
        // given
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now();
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }

    @Test
    void withFormatType_ShouldCreateValidSpecification() {
        // given
        ResourceSearchCriteria criteria = ResourceSearchCriteria.builder()
                .formatType("image/jpeg")
                .build();

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }

    @Test
    void withMultipleCriteria_ShouldCombineSpecifications() {
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

        // when
        Specification<ResourceJpaEntity> spec = ResourceSpecification.withCriteria(criteria);

        // then
        assertThat(spec).isNotNull();
    }
}