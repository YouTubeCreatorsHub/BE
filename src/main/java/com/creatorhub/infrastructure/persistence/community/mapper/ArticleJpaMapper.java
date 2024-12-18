package com.creatorhub.infrastructure.persistence.community.mapper;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ArticleJpaMapper {

    @Mapping(target = "board", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ArticleJpaEntity toJpaEntity(Article domain);

    @Mapping(target = "board", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Article toDomain(ArticleJpaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJpaEntity(@MappingTarget ArticleJpaEntity target, Article source);
}