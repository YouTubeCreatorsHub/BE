package com.creatorhub.infrastructure.persistence.resource.mapper;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.presentation.resource.dto.CreateResourceRequest;
import com.creatorhub.presentation.resource.dto.UpdateResourceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ResourceMapperTest {

    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    void toCommand_ShouldMapCreateRequestToCommand() {
        // given
        ResourceMetadata metadata = ResourceMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        CreateResourceRequest request = CreateResourceRequest.builder()
                .name("test-resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .metadata(metadata)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        // when
        CreateResourceCommand command = resourceMapper.toCommand(request, file);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getType()).isEqualTo(request.getType());
        assertThat(command.getLicenseType()).isEqualTo(request.getLicenseType());
        assertThat(command.getFile()).isEqualTo(file);
    }

    @Test
    void toEntity_ShouldMapCommandToEntity() {
        // given
        ResourceMetadata metadata = ResourceMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        CreateResourceCommand command = CreateResourceCommand.builder()
                .name("test-resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .metadata(metadata)
                .build();

        // when
        ResourceEntity entity = resourceMapper.toEntity(command);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(command.getName());
        assertThat(entity.getType()).isEqualTo(command.getType());
        assertThat(entity.getLicenseType()).isEqualTo(command.getLicenseType());
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // given
        ResourceMetadata metadata = ResourceMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        ResourceEntity entity = ResourceEntity.builder()
                .id(UUID.randomUUID())
                .name("test-resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .url("http://example.com/test.jpg")
                .thumbnailUrl("http://example.com/thumb.jpg")
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // when
        ResourceResponse response = resourceMapper.toResponse(entity);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getName()).isEqualTo(entity.getName());
        assertThat(response.getType()).isEqualTo(entity.getType());
        assertThat(response.getLicenseType()).isEqualTo(entity.getLicenseType());
        assertThat(response.getUrl()).isEqualTo(entity.getUrl());
        assertThat(response.getThumbnailUrl()).isEqualTo(entity.getThumbnailUrl());
    }

    @Test
    void toCommand_ShouldMapUpdateRequestToCommand() {
        // given
        ResourceMetadata metadata = ResourceMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        UpdateResourceRequest request = UpdateResourceRequest.builder()
                .name("updated-resource")
                .type(ResourceType.VIDEO)
                .licenseType(LicenseType.PREMIUM)
                .metadata(metadata)
                .build();

        // when
        UpdateResourceCommand command = resourceMapper.toCommand(request);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getType()).isEqualTo(request.getType());
        assertThat(command.getLicenseType()).isEqualTo(request.getLicenseType());
    }
}