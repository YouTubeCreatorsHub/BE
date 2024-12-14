package com.creatorhub.infrastructure.persistence.community.mapper;

import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.infrastructure.persistence.community.entity.MemberJpaEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
                ArticleJpaMapper.class,
                CommentJpaMapper.class
        }
)
public interface MemberJpaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "comments", ignore = true)
    MemberJpaEntity toJpaEntity(Member domain);

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Member toDomain(MemberJpaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJpaEntity(@MappingTarget MemberJpaEntity target, Member source);
}