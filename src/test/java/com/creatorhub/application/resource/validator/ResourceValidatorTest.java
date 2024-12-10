package com.creatorhub.application.resource.validator;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class ResourceValidatorTest {

    private final ResourceValidator resourceValidator = new ResourceValidator();

    @Test
    void validateCreateCommand_WithValidCommand_ShouldPass() {
        // given
        CreateResourceCommand command = CreateResourceCommand.builder()
                .name("test-resource")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .file(new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes()))
                .build();

        // when & then
        assertThatCode(() -> resourceValidator.validateCreateCommand(command))
                .doesNotThrowAnyException();
    }

    @Test
    void validateCreateCommand_WithEmptyName_ShouldThrowException() {
        // given
        CreateResourceCommand command = CreateResourceCommand.builder()
                .name("")
                .type(ResourceType.IMAGE)
                .licenseType(LicenseType.FREE)
                .build();

        // when & then
        assertThatThrownBy(() -> resourceValidator.validateCreateCommand(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }

    @Test
    void validateCreateCommand_WithoutType_ShouldThrowException() {
        // given
        CreateResourceCommand command = CreateResourceCommand.builder()
                .name("test-resource")
                .licenseType(LicenseType.FREE)
                .build();

        // when & then
        assertThatThrownBy(() -> resourceValidator.validateCreateCommand(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }

    @Test
    void validateCreateCommand_WithoutLicenseType_ShouldThrowException() {
        // given
        CreateResourceCommand command = CreateResourceCommand.builder()
                .name("test-resource")
                .type(ResourceType.IMAGE)
                .build();

        // when & then
        assertThatThrownBy(() -> resourceValidator.validateCreateCommand(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }
}