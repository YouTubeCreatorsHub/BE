package com.creatorhub.infrastructure.persistence.community.mapper;

import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BoardJpaMapper {

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    BoardJpaEntity toJpaEntity(Board domain);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "articles", ignore = true)
    Board toDomain(BoardJpaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJpaEntity(@MappingTarget BoardJpaEntity target, Board source);
}