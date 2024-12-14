package com.creatorhub.infrastructure.storage.s3;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class FileDownloadControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3StorageAdapter s3StorageAdapter;

    @Test
    void downloadFile_Success() throws Exception {
        String fileName = "test.jpg";
        String presignedUrl = "https://test-bucket.s3.amazonaws.com/test.jpg?token=xyz";

        when(s3StorageAdapter.getFileUrl(fileName)).thenReturn(presignedUrl);

        mockMvc.perform(get("/api/v1/files/{fileName}", fileName)
                .with(csrf()))  // with()는 MockHttpServletRequestBuilder의 메서드로 사용
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, presignedUrl));
    }

    @Test
    void downloadFile_NotFound() throws Exception {
        String fileName = "nonexistent.jpg";

        when(s3StorageAdapter.getFileUrl(fileName))
                .thenThrow(new BusinessException(ErrorCode.FILE_NOT_FOUND));

        mockMvc.perform(get("/api/v1/files/{fileName}", fileName)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}