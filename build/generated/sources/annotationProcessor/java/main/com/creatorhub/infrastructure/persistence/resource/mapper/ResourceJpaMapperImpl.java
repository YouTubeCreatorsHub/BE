package com.creatorhub.infrastructure.persistence.resource.mapper;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-27T23:55:16+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.5 (Azul Systems, Inc.)"
)
@Component
public class ResourceJpaMapperImpl implements ResourceJpaMapper {

    @Override
    public ResourceEntity toDomain(ResourceJpaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ResourceEntity.ResourceEntityBuilder<?, ?> resourceEntity = ResourceEntity.builder();

        resourceEntity.id( entity.getId() );
        resourceEntity.createdAt( entity.getCreatedAt() );
        resourceEntity.updatedAt( entity.getUpdatedAt() );
        resourceEntity.createdBy( entity.getCreatedBy() );
        resourceEntity.modifiedBy( entity.getModifiedBy() );
        resourceEntity.deleted( entity.isDeleted() );
        resourceEntity.name( entity.getName() );
        resourceEntity.type( entity.getType() );
        resourceEntity.url( entity.getUrl() );
        resourceEntity.licenseType( entity.getLicenseType() );
        resourceEntity.metadata( entity.getMetadata() );

        return resourceEntity.build();
    }

    @Override
    public ResourceJpaEntity toJpaEntity(ResourceEntity domain) {
        if ( domain == null ) {
            return null;
        }

        ResourceJpaEntity.ResourceJpaEntityBuilder<?, ?> resourceJpaEntity = ResourceJpaEntity.builder();

        resourceJpaEntity.id( domain.getId() );
        resourceJpaEntity.createdAt( domain.getCreatedAt() );
        resourceJpaEntity.updatedAt( domain.getUpdatedAt() );
        resourceJpaEntity.createdBy( domain.getCreatedBy() );
        resourceJpaEntity.modifiedBy( domain.getModifiedBy() );
        resourceJpaEntity.deleted( domain.isDeleted() );
        resourceJpaEntity.name( domain.getName() );
        resourceJpaEntity.type( domain.getType() );
        resourceJpaEntity.url( domain.getUrl() );
        resourceJpaEntity.licenseType( domain.getLicenseType() );
        resourceJpaEntity.metadata( domain.getMetadata() );

        return resourceJpaEntity.build();
    }
}
