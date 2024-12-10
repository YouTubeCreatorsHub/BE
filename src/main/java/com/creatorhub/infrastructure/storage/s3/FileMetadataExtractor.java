package com.creatorhub.infrastructure.storage.s3;

import com.creatorhub.domain.resource.vo.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FileMetadataExtractor {
    private final Tika tika = new Tika();

    public FileMetadata extractMetadata(MultipartFile file) throws IOException {
        String mimeType = tika.detect(file.getInputStream());
        long fileSize = file.getSize();
        String originalFilename = file.getOriginalFilename();

        return FileMetadata.builder()
                .fileName(originalFilename)
                .mimeType(mimeType)
                .size(fileSize)
                .extension(getFileExtension(originalFilename))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex + 1);
    }
}