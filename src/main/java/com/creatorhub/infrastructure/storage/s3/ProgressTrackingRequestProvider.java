package com.creatorhub.infrastructure.storage.s3;

import com.creatorhub.application.resource.service.FileUploadProgressTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class ProgressTrackingRequestProvider {
    public static RequestBody create(byte[] content, String fileName, FileUploadProgressTracker progressTracker) {
        progressTracker.updateProgress(fileName, 0, content.length);

        return RequestBody.fromContentProvider(
                () -> new ByteArrayInputStream(content),
                content.length,
                "application/octet-stream"
        );
    }
}