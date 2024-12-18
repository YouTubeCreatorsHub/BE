package com.creatorhub.application.community.member.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;

public class DuplicateMemberException extends BusinessException {
    public DuplicateMemberException(String field, String value) {
        super(ErrorCode.INVALID_INPUT, String.format("이미 사용 중인 %s입니다: %s", field, value));
    }
}