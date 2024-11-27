package com.creatorhub.infrastructure.persistence.resource.mapper;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResourceJpaMapper {
    ResourceEntity toDomain(ResourceJpaEntity entity);
    ResourceJpaEntity toJpaEntity(ResourceEntity domain);
}