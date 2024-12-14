package com.creatorhub.presentation.resource;

import com.creatorhub.application.resource.event.FileUploadProgressEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileUploadProgressListener {

    @EventListener
    public void handleFileUploadProgress(FileUploadProgressEvent event) {
        log.info("File: {}, Progress: {}%, Transferred: {}/{} bytes",
                event.getFileName(),
                event.getPercentCompleted(),
                event.getBytesTransferred(),
                event.getTotalBytes());
    }
}