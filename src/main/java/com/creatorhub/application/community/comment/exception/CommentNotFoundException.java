package com.creatorhub.application.community.comment.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

import java.util.UUID;

public class CommentNotFoundException extends BusinessException {
    public CommentNotFoundException(UUID id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("댓글을 찾을 수 없습니다. (ID: %s)", id));
    }
}
