package com.creatorhub.infrastructure.persistence.community.mapper;

import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CategoryJpaMapper {

    @Mapping(target = "board", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    CategoryJpaEntity toJpaEntity(Category domain);
    @Mapping(target = "board", ignore = true)
    @Mapping(target = "articles", ignore = true)
    Category toDomain(CategoryJpaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJpaEntity(@MappingTarget CategoryJpaEntity target, Category source);
}