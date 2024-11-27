package com.creatorhub.application.resource.dto.validator;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import org.springframework.stereotype.Component;

@Component
public class ResourceValidator {
    public void validateCreateCommand(CreateResourceCommand command) {
        // 검증 로직
    }
}