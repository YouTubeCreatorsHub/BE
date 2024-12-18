package com.creatorhub.infrastructure.storage.s3;

import com.creatorhub.application.resource.port.out.FileStoragePort;
import com.creatorhub.application.resource.service.FileUploadProgressTracker;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.ImageThumbnailGenerator;
import com.creatorhub.infrastructure.storage.s3.cache.ResourceCacheService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements FileStoragePort {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ResourceCacheService cacheService;
    private final FileUploadProgressTracker progressTracker;
    private final ImageThumbnailGenerator thumbnailGenerator;

    private static final String THUMBNAIL_PREFIX = "thumbnails/";
    private static final Logger log = LoggerFactory.getLogger(S3StorageAdapter.class);
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url-expiration:3600}")
    private long urlExpirationSeconds;
    @Override
    public String uploadFile(String fileName, byte[] content) {
        try {
            RequestBody requestBody = ProgressTrackingRequestProvider.create(
                    content,
                    fileName,
                    progressTracker
            );

            s3Client.putObject(req -> req
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    requestBody);
            // 이미지 파일인 경우 썸네일 생성 및 업로드
            if (isImage(fileName)) {
                uploadThumbnail(fileName, content);
            }

            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
        } catch (S3Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    private void uploadThumbnail(String fileName, byte[] content) {
        try {
            byte[] thumbnailContent = thumbnailGenerator.generateThumbnail(content);
            String thumbnailKey = THUMBNAIL_PREFIX + fileName;

            RequestBody thumbnailRequestBody = RequestBody.fromBytes(thumbnailContent);

            s3Client.putObject(req -> req
                            .bucket(bucketName)
                            .key(thumbnailKey)
                            .build(),
                    thumbnailRequestBody);

        } catch (Exception e) {
            log.warn("Failed to generate thumbnail for {}: {}", fileName, e.getMessage());
            // 썸네일 생성 실패는 원본 파일 업로드에 영향을 주지 않도록 함
        }
    }

    public String getThumbnailUrl(String fileName) {
        String thumbnailKey = THUMBNAIL_PREFIX + fileName;
        return cacheService.getCachedUrl(thumbnailKey, () -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        });
    }

    private boolean isImage(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".jpg") ||
                extension.endsWith(".jpeg") ||
                extension.endsWith(".png") ||
                extension.endsWith(".gif") ||
                extension.endsWith(".webp");
    }


    @Override
    public byte[] downloadFile(String fileName) {
        return cacheService.getCachedFile(fileName, () -> {
            try {
                ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(req -> req
                        .bucket(bucketName)
                        .key(fileName));
                return objectBytes.asByteArray();
            } catch (S3Exception e) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }
        });
    }

    @Override
    public void deleteFile(String fileName) {
        s3Client.deleteObject(req -> req
                .bucket(bucketName)
                .key(fileName));
    }

    @Override
    public String getFileUrl(String fileName) {
        return cacheService.getCachedUrl(fileName, () -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        });
    }
}