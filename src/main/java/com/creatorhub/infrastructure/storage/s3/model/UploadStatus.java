package com.creatorhub.infrastructure.storage.s3.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
public class UploadStatus {
    private final String fileName;
    private final long uploadedBytes;
    private final long totalBytes;
    private final int progress;
    private final String resourceUrl;
    private final String status;
    private final String errorMessage;

    // 진행상황 업데이트를 위한 새로운 생성자 추가
    public UploadStatus(String fileName, long uploadedBytes, long totalBytes, int progress, String resourceUrl) {
        this.fileName = fileName;
        this.uploadedBytes = uploadedBytes;
        this.totalBytes = totalBytes;
        this.progress = progress;
        this.resourceUrl = resourceUrl;
        this.status = "UPLOADING";
        this.errorMessage = null;
    }

    // 에러 상태를 위한 생성자
    public UploadStatus(String fileName, String errorMessage) {
        this.fileName = fileName;
        this.uploadedBytes = 0;
        this.totalBytes = 0;
        this.progress = 0;
        this.resourceUrl = null;
        this.status = "ERROR";
        this.errorMessage = errorMessage;
    }
}