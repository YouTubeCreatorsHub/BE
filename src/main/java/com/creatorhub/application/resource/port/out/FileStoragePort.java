package com.creatorhub.application.resource.port.out;

public interface FileStoragePort {
    String uploadFile(String fileName, byte[] content);
    byte[] downloadFile(String fileName);
    void deleteFile(String fileName);
    String getFileUrl(String fileName);
}