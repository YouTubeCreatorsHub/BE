package com.creatorhub.infrastructure.storage.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URL;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileDownloadController {

    private final S3StorageAdapter s3StorageAdapter;

    @GetMapping("/{fileName}")
    public ResponseEntity<Void> downloadFile(@PathVariable("fileName") String fileName) {
        String presignedUrl = s3StorageAdapter.getFileUrl(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, presignedUrl);

        return ResponseEntity
                .status(302)
                .headers(headers)
                .build();
    }
}