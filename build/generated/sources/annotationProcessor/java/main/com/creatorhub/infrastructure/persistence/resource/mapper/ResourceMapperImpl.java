package com.creatorhub.infrastructure.persistence.resource.mapper;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import com.creatorhub.presentation.resource.dto.CreateResourceRequest;
import com.creatorhub.presentation.resource.dto.UpdateResourceRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-27T23:55:16+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.5 (Azul Systems, Inc.)"
)
@Component
public class ResourceMapperImpl implements ResourceMapper {

    @Override
    public CreateResourceCommand toCommand(CreateResourceRequest request, MultipartFile file) {
        if ( request == null && file == null ) {
            return null;
        }

        CreateResourceCommand.CreateResourceCommandBuilder createResourceCommand = CreateResourceCommand.builder();

        if ( request != null ) {
            createResourceCommand.name( request.getName() );
            createResourceCommand.type( request.getType() );
            createResourceCommand.licenseType( request.getLicenseType() );
            createResourceCommand.metadata( request.getMetadata() );
        }
        createResourceCommand.file( file );

        return createResourceCommand.build();
    }

    @Override
    public ResourceEntity toEntity(CreateResourceCommand command) {
        if ( command == null ) {
            return null;
        }

        ResourceEntity.ResourceEntityBuilder<?, ?> resourceEntity = ResourceEntity.builder();

        resourceEntity.name( command.getName() );
        resourceEntity.type( command.getType() );
        resourceEntity.licenseType( command.getLicenseType() );
        resourceEntity.metadata( command.getMetadata() );

        return resourceEntity.build();
    }

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
    public ResourceJpaEntity toJpaEntity(CreateResourceCommand command) {
        if ( command == null ) {
            return null;
        }

        ResourceJpaEntity.ResourceJpaEntityBuilder<?, ?> resourceJpaEntity = ResourceJpaEntity.builder();

        resourceJpaEntity.name( command.getName() );
        resourceJpaEntity.type( command.getType() );
        resourceJpaEntity.licenseType( command.getLicenseType() );
        resourceJpaEntity.metadata( command.getMetadata() );

        return resourceJpaEntity.build();
    }

    @Override
    public ResourceJpaEntity toJpaEntity(ResourceEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ResourceJpaEntity.ResourceJpaEntityBuilder<?, ?> resourceJpaEntity = ResourceJpaEntity.builder();

        resourceJpaEntity.id( entity.getId() );
        resourceJpaEntity.createdAt( entity.getCreatedAt() );
        resourceJpaEntity.updatedAt( entity.getUpdatedAt() );
        resourceJpaEntity.createdBy( entity.getCreatedBy() );
        resourceJpaEntity.modifiedBy( entity.getModifiedBy() );
        resourceJpaEntity.deleted( entity.isDeleted() );
        resourceJpaEntity.name( entity.getName() );
        resourceJpaEntity.type( entity.getType() );
        resourceJpaEntity.url( entity.getUrl() );
        resourceJpaEntity.licenseType( entity.getLicenseType() );
        resourceJpaEntity.metadata( entity.getMetadata() );

        return resourceJpaEntity.build();
    }

    @Override
    public ResourceResponse toResponse(ResourceEntity domain) {
        if ( domain == null ) {
            return null;
        }

        ResourceResponse.ResourceResponseBuilder resourceResponse = ResourceResponse.builder();

        resourceResponse.id( domain.getId() );
        resourceResponse.name( domain.getName() );
        resourceResponse.type( domain.getType() );
        resourceResponse.url( domain.getUrl() );
        resourceResponse.licenseType( domain.getLicenseType() );
        resourceResponse.metadata( domain.getMetadata() );
        resourceResponse.createdAt( domain.getCreatedAt() );
        resourceResponse.updatedAt( domain.getUpdatedAt() );

        return resourceResponse.build();
    }

    @Override
    public UpdateResourceCommand toCommand(UpdateResourceRequest request) {
        if ( request == null ) {
            return null;
        }

        UpdateResourceCommand.UpdateResourceCommandBuilder updateResourceCommand = UpdateResourceCommand.builder();

        updateResourceCommand.name( request.getName() );
        updateResourceCommand.type( request.getType() );
        updateResourceCommand.licenseType( request.getLicenseType() );
        updateResourceCommand.metadata( request.getMetadata() );

        return updateResourceCommand.build();
    }
}
