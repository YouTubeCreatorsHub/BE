package com.creatorhub.presentation.resource.controller;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.service.ResourceService;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceMapper;
import com.creatorhub.infrastructure.storage.s3.service.image.TestSecurityConfig;
import com.creatorhub.presentation.resource.dto.CreateResourceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class ResourceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private ResourceMapper resourceMapper;


    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        CreateResourceRequest request = CreateResourceRequest.builder()
                .name("test.jpg")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
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

        when(resourceMapper.toCommand(any(), any())).thenReturn(
                CreateResourceCommand.builder()
                        .name("test.jpg")
                        .type(ResourceType.IMAGE)
                        .licenseType(LicenseType.FREE)
                        .file(file)
                        .build()
        );
        when(resourceService.createResource(any())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }
}