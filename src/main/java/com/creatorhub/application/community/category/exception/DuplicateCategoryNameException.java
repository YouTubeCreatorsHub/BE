package com.creatorhub.application.community.category.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

import java.util.UUID;

public class DuplicateCategoryNameException extends BusinessException {
    public DuplicateCategoryNameException(String name, UUID boardId) {
        super(ErrorCode.INVALID_INPUT,
                String.format("해당 게시판에 이미 존재하는 카테고리 이름입니다: %s (게시판 ID: %s)", name, boardId));
    }
}