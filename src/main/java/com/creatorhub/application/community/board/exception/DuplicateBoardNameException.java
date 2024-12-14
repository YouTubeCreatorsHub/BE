package com.creatorhub.application.community.board.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

public class DuplicateBoardNameException extends BusinessException {
    public DuplicateBoardNameException(String name) {
        super(ErrorCode.INVALID_INPUT, String.format("이미 존재하는 게시판 이름입니다: %s", name));
    }
}