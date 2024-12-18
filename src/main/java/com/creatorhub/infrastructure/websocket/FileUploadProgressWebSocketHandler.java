package com.creatorhub.infrastructure.websocket;

import com.creatorhub.application.resource.event.FileUploadProgressEvent;
import com.creatorhub.infrastructure.storage.s3.model.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUploadProgressWebSocketHandler {
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleFileUploadProgress(FileUploadProgressEvent event) {
        UploadStatus status = new UploadStatus(
                event.getFileName(),
                event.getBytesTransferred(),
                event.getTotalBytes(),
                event.getPercentCompleted(),
                null
        );

        messagingTemplate.convertAndSend(
                "/topic/upload-progress/" + event.getFileName(),
                status
        );
    }
}