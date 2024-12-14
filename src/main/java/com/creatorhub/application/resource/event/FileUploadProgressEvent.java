package com.creatorhub.application.resource.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileUploadProgressEvent {
    private final String fileName;
    private final long bytesTransferred;
    private final long totalBytes;
    private final int percentCompleted;
}