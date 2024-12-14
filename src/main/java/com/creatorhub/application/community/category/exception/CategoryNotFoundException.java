package com.creatorhub.application.community.category.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

import java.util.UUID;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(UUID id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("카테고리를 찾을 수 없습니다. (ID: %s)", id));
    }
}