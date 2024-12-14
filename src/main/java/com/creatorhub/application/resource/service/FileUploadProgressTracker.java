package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.event.FileUploadProgressEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileUploadProgressTracker {
    private final ApplicationEventPublisher eventPublisher;

    public void updateProgress(String fileName, long bytesTransferred, long totalBytes) {
        int percentCompleted = (int) ((bytesTransferred * 100) / totalBytes);
        FileUploadProgressEvent event = new FileUploadProgressEvent(
                fileName,
                bytesTransferred,
                totalBytes,
                percentCompleted
        );
        eventPublisher.publishEvent(event);
    }
}