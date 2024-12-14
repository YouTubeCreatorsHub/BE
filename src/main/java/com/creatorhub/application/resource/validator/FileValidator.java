package com.creatorhub.application.resource.validator;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.resource.vo.ResourceType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import com.creatorhub.domain.common.error.ErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class FileValidator {
    private static final Map<ResourceType, List<String>> ALLOWED_CONTENT_TYPES = Map.of(
            ResourceType.MUSIC, Arrays.asList("audio/mpeg", "audio/wav", "audio/ogg"),
            ResourceType.IMAGE, Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp"),
            ResourceType.VIDEO, Arrays.asList("video/mp4", "video/webm", "video/mpeg"),
            ResourceType.TEMPLATE, Arrays.asList("application/json"),
            ResourceType.FONT, Arrays.asList("font/ttf", "font/otf", "font/woff", "font/woff2")
    );

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    public void validateFile(MultipartFile file, ResourceType resourceType) {
        validateFileSize(file);
        validateFileType(file, resourceType);
        validateFileContent(file);
    }

    private void validateFileType(MultipartFile file, ResourceType resourceType) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.get(resourceType).contains(contentType)) {
            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE,
                    String.format("지원하지 않는 파일 형식입니다. 허용된 형식: %s",
                            String.join(", ", ALLOWED_CONTENT_TYPES.get(resourceType)))
            );
        }
    }

    private void validateFileContent(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "파일이 비어있습니다.");
        }
    }

    public void validateFileSize(MultipartFile file) {
        long size = file.getSize();
        long maxSize = parseSize(maxFileSize);

        if (size > maxSize) {
            throw new BusinessException(
                    ErrorCode.FILE_SIZE_EXCEEDED,
                    String.format("파일 크기는 %s를 초과할 수 없습니다.", maxFileSize)
            );
        }
    }

    private long parseSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(size);
    }
}