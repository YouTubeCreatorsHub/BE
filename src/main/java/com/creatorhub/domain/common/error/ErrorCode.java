package com.creatorhub.domain.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common Errors
    INVALID_INPUT(1001, HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    RESOURCE_NOT_FOUND(1002, HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    UNAUTHORIZED(1003, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(1004, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_PARAMETER(1005, HttpStatus.BAD_REQUEST, "잘못된 파라미터입니다."),
    INTERNAL_SERVER_ERROR(1006, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),

    // Resource Domain Errors
    INVALID_RESOURCE_TYPE(2001, HttpStatus.BAD_REQUEST, "유효하지 않은 리소스 타입입니다."),
    RESOURCE_UPLOAD_FAILED(2002, HttpStatus.INTERNAL_SERVER_ERROR, "리소스 업로드에 실패했습니다."),
    INVALID_LICENSE_TYPE(2003, HttpStatus.BAD_REQUEST, "유효하지 않은 라이센스 타입입니다."),

    // File Domain Errors
    FILE_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_UPLOAD_ERROR(3002, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE(3003, HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(3004, HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),

    // Image Errors
    INVALID_IMAGE_FORMAT(3005, HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 형식입니다."),
    THUMBNAIL_GENERATION_FAILED(3006, HttpStatus.INTERNAL_SERVER_ERROR, "썸네일 생성에 실패했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}