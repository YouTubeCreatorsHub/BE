package com.creatorhub.infrastructure.integration;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.service.FileUploadProgressSender;
import com.creatorhub.application.resource.service.ResourceService;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.storage.s3.S3StorageAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class ResourceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private FileUploadProgressSender progressSender;

    @MockBean
    private S3StorageAdapter s3StorageAdapter;


    @Test
    void shouldIntegrateAllComponents() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String requestJson = """
                {
                    "type": "IMAGE",
                    "licenseType": "FREE",
                    "name": "test.jpg"
                }""";

        MockMultipartFile request = new MockMultipartFile(
                "request",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        ResourceResponse mockResponse = ResourceResponse.builder()
                .id(UUID.randomUUID())
                .name("test.jpg")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .url("https://test-url.com/test.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(resourceService.createResource(any())).thenAnswer(invocation -> {
            progressSender.sendProgress("test.jpg", 0L, 100L, null);
            progressSender.sendProgress("test.jpg", 100L, 100L, "thumbnail-url");
            return mockResponse;
        });

        // When
        mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .file(request)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        // Then
        verify(progressSender).sendProgress(anyString(), eq(0L), anyLong(), isNull());
        verify(progressSender).sendProgress(anyString(), anyLong(), anyLong(), anyString());
    }
}