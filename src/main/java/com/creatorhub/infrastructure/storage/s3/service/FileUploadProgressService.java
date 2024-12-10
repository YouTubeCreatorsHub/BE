package com.creatorhub.infrastructure.storage.s3.service;

import com.creatorhub.infrastructure.storage.s3.model.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileUploadProgressService {
    private final SimpMessagingTemplate messagingTemplate;

    public void updateProgress(String fileName, long uploadedBytes, long totalBytes, String resourceUrl) {
        int progress = (int) ((uploadedBytes * 100) / totalBytes);
        UploadStatus status = new UploadStatus(fileName, uploadedBytes, totalBytes, progress, resourceUrl);
        messagingTemplate.convertAndSend("/topic/upload-progress/" + fileName, status);
    }

    public void notifyError(String fileName, String errorMessage) {
        UploadStatus status = new UploadStatus(fileName, errorMessage);
        messagingTemplate.convertAndSend("/topic/upload-progress/" + fileName, status);
    }

    public void notifyComplete(String fileName, String resourceUrl) {
        UploadStatus status = new UploadStatus(fileName, resourceUrl);
        messagingTemplate.convertAndSend("/topic/upload-progress/" + fileName, status);
    }
}