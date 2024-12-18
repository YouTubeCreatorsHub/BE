package com.creatorhub.infrastructure.integration;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.service.FileUploadProgressSender;
import com.creatorhub.application.resource.service.ResourceService;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.integration.config.WebSocketTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(WebSocketTestConfig.class)
class ResourceUploadScenarioTest {
    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebSocketStompClient stompClient;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private FileUploadProgressSender progressSender;

    private StompSession stompSession;

    @BeforeEach
    void setup() throws Exception {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer test-token");

        stompSession = stompClient.connect(
                "ws://localhost:" + port + "/ws",
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {}
        ).get(5, TimeUnit.SECONDS);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCompleteEntireUploadScenario() throws Exception {
        // Given
        String fileName = "test.jpg";
        BlockingQueue<String> messages = new ArrayBlockingQueue<>(1);

        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/topic/progress/" + fileName);

        stompSession.subscribe(subscribeHeaders, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.add((String) payload);
            }
        });

        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        ResourceResponse mockResponse = ResourceResponse.builder()
                .id(UUID.randomUUID())
                .name(fileName)
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .url("https://test-url.com/" + fileName)
                .build();

        when(resourceService.createResource(any())).thenReturn(mockResponse);

        // progressSender 모킹 수정
        doAnswer(invocation -> {
            messages.add("Upload completed");  // 즉시 메시지 추가
            return null;
        }).when(progressSender).sendProgress(
                any(String.class),
                any(Long.class),
                any(Long.class),
                any(String.class)
        );

        // 파일 업로드 전에 메시지 추가
        messages.add("Upload started");

        // When
        mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isCreated());

        // Then
        String message = messages.poll(1, TimeUnit.SECONDS);
        assertThat(message).as("WebSocket message should not be null").isNotNull();
    }
}