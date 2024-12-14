package com.creatorhub.application.community.member.exception;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import java.util.UUID;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException(UUID id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("회원을 찾을 수 없습니다. (ID: %s)", id));
    }

    public MemberNotFoundException(String email) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("회원을 찾을 수 없습니다. (Email: %s)", email));
    }
}