package com.creatorhub.presentation.common.error;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.common.response.ErrorResponse;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.security.access.AccessDeniedException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMultipartException(MultipartException e) {
        log.error("Multipart Exception: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.FILE_UPLOAD_ERROR,
                "파일 업로드 중 오류가 발생했습니다."
        );
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        log.error("File size limit exceeded: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.FILE_SIZE_EXCEEDED,
                "파일 크기가 제한을 초과했습니다."
        );
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(ApiResponse.error(errorResponse), e.getErrorCode().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation Exception: {}", e.getMessage(), e);
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT, details);
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                e.getMessage()
        );
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access Denied Exception: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.FORBIDDEN,
                "접근 권한이 없습니다."
        );
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.FORBIDDEN);
    }
}