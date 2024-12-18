package com.creatorhub.infrastructure.storage.s3;

import com.creatorhub.application.resource.event.FileUploadProgressMessage;
import com.creatorhub.application.resource.service.FileUploadProgressSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
class FileUploadWebSocketTest {

    @LocalServerPort
    private int port;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private FileUploadProgressSender progressSender;

    @Value("${local.server.port}")
    private int localServerPort;

    @Test
    void testProgressUpdate() {
        String fileName = "test.jpg";
        long bytesTransferred = 50L;
        long totalBytes = 100L;
        String thumbnailUrl = "http://example.com/thumb.jpg";

        progressSender.sendProgress(fileName, bytesTransferred, totalBytes, thumbnailUrl);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/progress/" + fileName),
                any(FileUploadProgressMessage.class)
        );
    }

    @Test
    void testErrorMessage() {
        String fileName = "test.jpg";
        String errorMessage = "Upload failed";

        progressSender.sendError(fileName, errorMessage);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/progress/" + fileName),
                any(FileUploadProgressMessage.class)
        );
    }
}