package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.event.FileUploadProgressMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUploadProgressSender {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendProgress(String fileName, long bytesTransferred, long totalBytes, String thumbnailUrl) {
        int percentCompleted = (int) ((bytesTransferred * 100) / totalBytes);

        FileUploadProgressMessage message = FileUploadProgressMessage.builder()
                .fileName(fileName)
                .bytesTransferred(bytesTransferred)
                .totalBytes(totalBytes)
                .percentCompleted(percentCompleted)
                .thumbnailUrl(thumbnailUrl)
                .status(percentCompleted == 100 ? "COMPLETED" : "PROCESSING")
                .build();

        messagingTemplate.convertAndSend("/topic/progress/" + fileName, message);
    }

    public void sendError(String fileName, String error) {
        FileUploadProgressMessage message = FileUploadProgressMessage.builder()
                .fileName(fileName)
                .status("FAILED")
                .build();

        messagingTemplate.convertAndSend("/topic/progress/" + fileName, message);
    }
}