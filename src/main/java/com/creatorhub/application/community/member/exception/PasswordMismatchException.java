package com.creatorhub.application.community.member.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException() {
        super(ErrorCode.INVALID_INPUT, "현재 비밀번호가 일치하지 않습니다.");
    }

    public PasswordMismatchException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }
}