package com.creatorhub.infrastructure.persistence.resource.specification;

import com.creatorhub.application.resource.dto.ResourceSearchCriteria;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ResourceSpecification {

    public static Specification<ResourceJpaEntity> withCriteria(ResourceSearchCriteria criteria) {
        return Specification.where(withName(criteria.getName()))
                .and(withType(criteria.getType()))
                .and(withLicenseType(criteria.getLicenseType()))
                .and(withSizeRange(criteria.getMinSize(), criteria.getMaxSize()))
                .and(withDateRange(criteria.getFromDate(), criteria.getToDate()))
                .and(withFormatType(criteria.getFormatType()));
    }

    private static Specification<ResourceJpaEntity> withName(String name) {
        return (root, query, cb) -> {
            if (name == null) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    private static Specification<ResourceJpaEntity> withType(ResourceType type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            return cb.equal(root.get("type"), type);
        };
    }

    private static Specification<ResourceJpaEntity> withLicenseType(LicenseType licenseType) {
        return (root, query, cb) -> {
            if (licenseType == null) return null;
            return cb.equal(root.get("licenseType"), licenseType);
        };
    }

    private static Specification<ResourceJpaEntity> withSizeRange(Long minSize, Long maxSize) {
        return (root, query, cb) -> {
            if (minSize == null && maxSize == null) return null;
            if (minSize == null) return cb.lessThanOrEqualTo(root.get("metadata").get("size"), maxSize);
            if (maxSize == null) return cb.greaterThanOrEqualTo(root.get("metadata").get("size"), minSize);
            return cb.between(root.get("metadata").get("size"), minSize, maxSize);
        };
    }

    private static Specification<ResourceJpaEntity> withDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) return null;
            if (fromDate == null) return cb.lessThanOrEqualTo(root.get("createdAt"), toDate);
            if (toDate == null) return cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
            return cb.between(root.get("createdAt"), fromDate, toDate);
        };
    }

    private static Specification<ResourceJpaEntity> withFormatType(String formatType) {
        return (root, query, cb) -> {
            if (formatType == null) return null;
            return cb.equal(root.get("metadata").get("format"), formatType);
        };
    }
}