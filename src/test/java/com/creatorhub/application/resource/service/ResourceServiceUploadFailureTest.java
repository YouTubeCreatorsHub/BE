package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.port.out.FileStoragePort;
import com.creatorhub.application.resource.validator.FileValidator;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.ThumbnailGenerator;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.repository.ResourceRepository;
import com.creatorhub.domain.resource.vo.FileMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceMapper;
import com.creatorhub.infrastructure.persistence.resource.repository.ResourceJpaRepository;
import com.creatorhub.infrastructure.storage.s3.FileMetadataExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;


import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceUploadFailureTest {
    @InjectMocks
    private ResourceService resourceService;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ThumbnailGenerator thumbnailGenerator;

    @Mock
    private Optional<FileStoragePort> fileStoragePort;

    @Mock
    private FileMetadataExtractor metadataExtractor;

    @Mock
    private FileUploadProgressSender progressSender;

    @Test
    void uploadFile_WithInvalidFileType_ShouldThrowException() throws IOException {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.invalid",
                "application/octet-stream",
                "test data".getBytes()
        );

        CreateResourceCommand command = CreateResourceCommand.builder()
                .file(invalidFile)
                .type(ResourceType.IMAGE)
                .build();

        // ResourceEntity mock 설정
        ResourceEntity mockEntity = mock(ResourceEntity.class);
        when(resourceMapper.toEntity(any())).thenReturn(mockEntity);

        // validateFileSize에서 예외를 던지도록 설정
        doThrow(new BusinessException(ErrorCode.INVALID_FILE_TYPE))
                .when(fileValidator).validateFileSize(any());

        assertThatThrownBy(() -> resourceService.createResource(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_FILE_TYPE);
    }

    @Test
    void uploadFile_WithExceededFileSize_ShouldThrowException() throws IOException {
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                new byte[10 * 1024 * 1024]
        );
        CreateResourceCommand command = CreateResourceCommand.builder()
                .file(largeFile)
                .type(ResourceType.IMAGE)
                .build();

        doThrow(new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED))
                .when(fileValidator).validateFileSize(any());

        assertThatThrownBy(() -> resourceService.createResource(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_SIZE_EXCEEDED);
    }

    @Test
    void uploadFile_WithS3Error_ShouldThrowException() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );
        CreateResourceCommand command = CreateResourceCommand.builder()
                .file(file)
                .type(ResourceType.IMAGE)
                .build();

        // ResourceEntity mock 설정
        ResourceEntity mockEntity = mock(ResourceEntity.class);
        when(resourceMapper.toEntity(any())).thenReturn(mockEntity);

        // validateFileSize mock 설정
        doNothing().when(fileValidator).validateFileSize(any());

        // FileMetadata mock 설정
        FileMetadata mockMetadata = FileMetadata.builder()
                .mimeType("image/jpeg")
                .size(file.getSize())
                .build();
        when(metadataExtractor.extractMetadata(any())).thenReturn(mockMetadata);

        // FileStoragePort mock 설정
        FileStoragePort mockFileStoragePort = mock(FileStoragePort.class);
        when(fileStoragePort.isPresent()).thenReturn(true);
        when(fileStoragePort.get()).thenReturn(mockFileStoragePort);

        // IOException으로 감싸서 throw
        doAnswer(invocation -> {
            throw new IOException("Upload failed");
        }).when(mockFileStoragePort).uploadFile(any(), any());

        assertThatThrownBy(() -> resourceService.createResource(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_UPLOAD_ERROR);
    }

    @Test
    void uploadFile_WithThumbnailGenerationError_ShouldThrowException() throws IOException {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );
        CreateResourceCommand command = CreateResourceCommand.builder()
                .file(imageFile)
                .type(ResourceType.IMAGE)
                .build();

        // ResourceEntity mock 설정
        ResourceEntity mockEntity = mock(ResourceEntity.class);
        when(resourceMapper.toEntity(any())).thenReturn(mockEntity);

        // validateFileSize mock 설정
        doNothing().when(fileValidator).validateFileSize(any());

        // FileMetadata mock 설정
        FileMetadata mockMetadata = FileMetadata.builder()
                .mimeType("image/jpeg")
                .size(imageFile.getSize())
                .build();
        when(metadataExtractor.extractMetadata(any())).thenReturn(mockMetadata);

        // FileStoragePort mock 설정 - Optional.get()이 실제 mock을 반환하도록
        FileStoragePort mockFileStoragePort = mock(FileStoragePort.class);
        when(fileStoragePort.isPresent()).thenReturn(true);
        when(fileStoragePort.get()).thenReturn(mockFileStoragePort);
        when(mockFileStoragePort.uploadFile(any(), any())).thenReturn("test-url");

        assertThatThrownBy(() -> resourceService.createResource(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.THUMBNAIL_GENERATION_FAILED);
    }
}

