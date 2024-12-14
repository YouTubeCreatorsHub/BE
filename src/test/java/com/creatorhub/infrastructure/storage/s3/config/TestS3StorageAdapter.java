package com.creatorhub.infrastructure.storage.s3.config;

import com.creatorhub.application.resource.service.FileUploadProgressTracker;
import com.creatorhub.domain.resource.ImageThumbnailGenerator;
import com.creatorhub.infrastructure.storage.s3.S3StorageAdapter;
import com.creatorhub.infrastructure.storage.s3.cache.ResourceCacheService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.mockito.Mockito.mock;

@Component
@Primary
@Profile("test")
public class TestS3StorageAdapter extends S3StorageAdapter {

    public TestS3StorageAdapter() {
        super(
                mock(S3Client.class),
                mock(S3Presigner.class),
                mock(ResourceCacheService.class),
                mock(FileUploadProgressTracker.class),
                mock(ImageThumbnailGenerator.class)
        );
    }

    @Override
    public String uploadFile(String fileName, byte[] content) {
        return "https://test-bucket.s3.amazonaws.com/" + fileName;
    }

    @Override
    public String getFileUrl(String fileName) {
        return "https://test-bucket.s3.amazonaws.com/" + fileName;
    }

    @Override
    public void deleteFile(String fileName) {
        // 테스트 환경에서는 아무 동작도 하지 않음
    }

    public boolean existsFile(String fileName) {
        return true;
    }
}