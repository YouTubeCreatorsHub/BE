package com.creatorhub.application.community.member.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedMemberModificationException extends BusinessException {
    public UnauthorizedMemberModificationException() {
        super(ErrorCode.FORBIDDEN, "회원 정보 수정 권한이 없습니다.");
    }
}