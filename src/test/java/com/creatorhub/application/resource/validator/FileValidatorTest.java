package com.creatorhub.application.resource.validator;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.vo.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class FileValidatorTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidator();
        ReflectionTestUtils.setField(fileValidator, "maxFileSize", "5MB");
    }

    @Test
    void validateFileSize_WithValidSize_ShouldPass() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[1024 * 1024] // 1MB
        );

        // when & then
        assertThatCode(() -> fileValidator.validateFileSize(file))
                .doesNotThrowAnyException();
    }

    @Test
    void validateFileSize_WithExceededSize_ShouldThrowException() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[6 * 1024 * 1024] // 6MB
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFileSize(file))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_SIZE_EXCEEDED);
    }

    @Test
    void validateFileType_WithValidType_ShouldPass() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        // when & then
        assertThatCode(() -> fileValidator.validateFile(file, ResourceType.IMAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void validateFileType_WithInvalidType_ShouldThrowException() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file, ResourceType.IMAGE))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_FILE_TYPE);
    }
}