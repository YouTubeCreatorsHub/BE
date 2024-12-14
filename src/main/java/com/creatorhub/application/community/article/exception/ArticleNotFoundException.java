package com.creatorhub.application.community.article.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

import java.util.UUID;

public class ArticleNotFoundException extends BusinessException {
    public ArticleNotFoundException(UUID id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("게시글을 찾을 수 없습니다. (ID: %s)", id));
    }
}
