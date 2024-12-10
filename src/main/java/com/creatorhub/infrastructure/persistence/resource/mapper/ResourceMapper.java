package com.creatorhub.infrastructure.persistence.resource.mapper;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.presentation.resource.dto.CreateResourceRequest;
import com.creatorhub.presentation.resource.dto.UpdateResourceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResourceMapper {
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "type", source = "request.type")
    @Mapping(target = "licenseType", source = "request.licenseType")
    @Mapping(target = "metadata", source = "request.metadata")
    CreateResourceCommand toCommand(CreateResourceRequest request, MultipartFile file);

    ResourceEntity toEntity(CreateResourceCommand command);
    ResourceResponse toResponse(ResourceEntity domain);

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "type", source = "request.type")
    @Mapping(target = "licenseType", source = "request.licenseType")
    UpdateResourceCommand toCommand(UpdateResourceRequest request);
}