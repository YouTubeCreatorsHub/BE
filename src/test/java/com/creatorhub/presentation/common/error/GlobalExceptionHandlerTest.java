package com.creatorhub.presentation.common.error;

import com.creatorhub.application.resource.service.ResourceService;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.infrastructure.common.config.JpaConfig;
import com.creatorhub.presentation.resource.ResourceController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.sql.DataSource;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {GlobalExceptionHandler.class, ResourceController.class})
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;


        @Test
        void handleBusinessException() throws Exception {
            // given
            BusinessException exception = new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            when(resourceService.getResource(any(UUID.class)))
                    .thenThrow(exception);

            // when & then
            mockMvc.perform(get("/api/v1/resources/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())  // 404로 수정
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.RESOURCE_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.RESOURCE_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        void handleValidationException() throws Exception {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test".getBytes()
            );

            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    "{}".getBytes()
            );

            when(resourceService.createResource(any()))
                    .thenThrow(new BusinessException(ErrorCode.INVALID_INPUT));

            // when & then
            mockMvc.perform(multipart("/api/v1/resources")
                            .file(file)
                            .file(request)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.getCode()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()))
                    .andDo(print());
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
                    new byte[6 * 1024 * 1024] // 6MB to trigger size exception
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



