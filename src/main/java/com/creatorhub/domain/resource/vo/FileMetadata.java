package com.creatorhub.domain.resource.vo;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class FileMetadata {
    private String fileName;
    private String mimeType;
    private long size;
    private String extension;
    private LocalDateTime createdAt;

    public boolean isValidMimeType() {
        return mimeType != null && !mimeType.isEmpty() &&
                ResourceType.getSupportedMimeTypes().contains(mimeType);
    }
}