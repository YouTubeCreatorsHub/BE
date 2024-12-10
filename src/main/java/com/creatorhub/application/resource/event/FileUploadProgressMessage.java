package com.creatorhub.application.resource.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadProgressMessage {
    private final String fileName;
    private final long bytesTransferred;
    private final long totalBytes;
    private final int percentCompleted;
    private final String thumbnailUrl;
    private final String status;  // PROCESSING, COMPLETED, FAILED
}