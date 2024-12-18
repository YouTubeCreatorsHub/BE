package com.creatorhub.application.community.board.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import java.util.UUID;

public class BoardNotFoundException extends BusinessException {
    public BoardNotFoundException(UUID id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("게시판을 찾을 수 없습니다. (ID: %s)", id));
    }
}