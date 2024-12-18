package com.creatorhub.application.community.comment.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

public class UnauthorizedCommentModificationException extends BusinessException {
    public UnauthorizedCommentModificationException() {
        super(ErrorCode.FORBIDDEN, "댓글 작성자만 수정/삭제할 수 있습니다.");
    }
}