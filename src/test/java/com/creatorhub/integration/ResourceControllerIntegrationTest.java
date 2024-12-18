package com.creatorhub.integration;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import com.creatorhub.presentation.resource.dto.CreateResourceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class ResourceControllerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리소스를 성공적으로 생성할 수 있다")
    @WithMockUser(roles = "USER")
    void createResource_Success() throws Exception {
        // given
        CreateResourceRequest request = new CreateResourceRequest();
        request.setName("Test Resource");
        request.setType(ResourceType.IMAGE);
        request.setLicenseType(LicenseType.FREE);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        MvcResult result = mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .file(requestPart))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        ResourceResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResourceResponse.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getType()).isEqualTo(request.getType());
        assertThat(response.getLicenseType()).isEqualTo(request.getLicenseType());
    }
}