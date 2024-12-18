package com.creatorhub.presentation.common.error;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.application.resource.service.ResourceService;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.presentation.resource.ResourceController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({GlobalExceptionHandler.class, ResourceController.class, TestController.class})
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private TestController testController;

    @Test
    @DisplayName("BusinessException이 발생하면 적절한 에러 응답을 반환한다")
    @WithMockUser
    void handleBusinessException() throws Exception {
        // given
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        doThrow(new BusinessException(errorCode))
                .when(testController)
                .throwBusinessException();

        // when
        MvcResult result = mockMvc.perform(get("/test/business-exception")
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus().value()))
                .andReturn();

        // then
        ApiResponse<Void> response = objectMapper.readValue(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                ApiResponse.class
        );

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getError().getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException이 발생하면 적절한 에러 응답을 반환한다")
    @WithMockUser
    void handleMethodArgumentNotValidException() throws Exception {
        // given
        MemberCommand.Create command = MemberCommand.Create.builder()
                .email("invalid-email")  // 잘못된 이메일 형식
                .password("short")       // 잘못된 비밀번호 형식
                .nickname("")           // 빈 문자열
                .build();

        // when & then
        mockMvc.perform(post("/test/validation-exception")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message").exists());
    }

    @Test
    void handleMultipartException() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test".getBytes()
        );

        when(resourceService.createResource(any()))
                .thenThrow(new MultipartException("파일 업로드 실패"));

        // when & then
        mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ErrorCode.FILE_UPLOAD_ERROR.getCode()))
                .andDo(print());
    }

    @Test
    void handleMaxUploadSizeExceededException() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[6 * 1024 * 1024]
        );

        when(resourceService.createResource(any()))
                .thenThrow(new MaxUploadSizeExceededException(1024L));

        // when & then
        mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ErrorCode.FILE_SIZE_EXCEEDED.getCode()))
                .andDo(print());
    }

    @Test
    void handleGeneralException() throws Exception {
        // given
        when(resourceService.getResource(any(UUID.class)))
                .thenThrow(new RuntimeException("unexpected error"));

        // when & then
        mockMvc.perform(get("/api/v1/resources/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ErrorCode.INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
                .andDo(print());
    }
}


