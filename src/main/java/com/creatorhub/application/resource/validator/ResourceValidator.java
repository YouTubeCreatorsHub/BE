package com.creatorhub.application.resource.validator;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ResourceValidator {
    public void validateCreateCommand(CreateResourceCommand command) {
        // 이름 검증
        if (command.getName() == null || command.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "리소스 이름은 필수입니다.");
        }

        // 타입 검증
        if (command.getType() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "리소스 타입은 필수입니다.");
        }

        // 라이센스 타입 검증
        if (command.getLicenseType() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "라이센스 타입은 필수입니다.");
        }

        // 파일 검증 (필요한 경우)
        if (command.getFile() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "파일은 필수입니다.");
        }
    }
}