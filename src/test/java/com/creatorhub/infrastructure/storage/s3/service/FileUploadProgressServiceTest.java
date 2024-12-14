package com.creatorhub.infrastructure.storage.s3.service;

import com.creatorhub.infrastructure.storage.s3.model.UploadStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileUploadProgressServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private FileUploadProgressService progressService;

    @Test
    void updateProgress_ShouldSendCorrectMessage() {
        // given
        String fileName = "test.jpg";
        long uploadedBytes = 50L;
        long totalBytes = 100L;
        String resourceUrl = "http://example.com/test.jpg";

        ArgumentCaptor<UploadStatus> statusCaptor = ArgumentCaptor.forClass(UploadStatus.class);

        // when
        progressService.updateProgress(fileName, uploadedBytes, totalBytes, resourceUrl);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/upload-progress/" + fileName),
                statusCaptor.capture()
        );

        UploadStatus captured = statusCaptor.getValue();
        assertThat(captured.getFileName()).isEqualTo(fileName);
        assertThat(captured.getUploadedBytes()).isEqualTo(uploadedBytes);
        assertThat(captured.getProgress()).isBetween(0, 100);
    }

    @Test
    void notifyError_ShouldSendErrorMessage() {
        // given
        String fileName = "test.jpg";
        String errorMessage = "Upload failed";

        ArgumentCaptor<UploadStatus> statusCaptor = ArgumentCaptor.forClass(UploadStatus.class);

        // when
        progressService.notifyError(fileName, errorMessage);

        // then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/upload-progress/" + fileName),
                statusCaptor.capture()
        );

        UploadStatus captured = statusCaptor.getValue();
        assertThat(captured.getFileName()).isEqualTo(fileName);
        assertThat(captured.getErrorMessage()).isEqualTo(errorMessage);
    }
}