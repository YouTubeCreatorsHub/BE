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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    @DisplayName("CreateResourceRequest를 CreateResourceCommand로 변환할 수 있다")
    void toCommand_FromCreateRequest() {
        // given
        CreateResourceRequest request = CreateResourceRequest.builder()
                .name("테스트 리소스")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .metadata(ResourceMetadata.createForTest())
                .build();

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
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
    @DisplayName("CreateResourceCommand를 ResourceEntity로 변환할 수 있다")
    void toEntity_FromCommand() {
        // given
        CreateResourceCommand command = CreateResourceCommand.builder()
                .name("테스트 리소스")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .metadata(ResourceMetadata.createForTest())
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
    @DisplayName("ResourceEntity를 ResourceResponse로 변환할 수 있다")
    void toResponse() {
        // given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ResourceEntity entity = ResourceEntity.builder()
                .id(id)
                .name("테스트 리소스")
                .type(ResourceType.IMAGE)
                .url("http://example.com/image.jpg")
                .licenseType(LicenseType.FREE)
                .metadata(ResourceMetadata.createForTest())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // when
        ResourceResponse response = resourceMapper.toResponse(entity);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getName()).isEqualTo(entity.getName());
        assertThat(response.getType()).isEqualTo(entity.getType());
        assertThat(response.getUrl()).isEqualTo(entity.getUrl());
        assertThat(response.getLicenseType()).isEqualTo(entity.getLicenseType());
        assertThat(response.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("UpdateResourceRequest를 UpdateResourceCommand로 변환할 수 있다")
    void toCommand_FromUpdateRequest() {
        // given
        UpdateResourceRequest request = UpdateResourceRequest.builder()
                .name("업데이트된 리소스")
                .type(ResourceType.VIDEO)
                .licenseType(LicenseType.PREMIUM)
                .metadata(ResourceMetadata.createForTest())
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