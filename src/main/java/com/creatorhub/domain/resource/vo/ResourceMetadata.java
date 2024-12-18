package com.creatorhub.domain.resource.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ResourceMetadata {
    private String format;
    private Long size;
    private String duration;
    private String resolution;
    private String additionalInfo;

    public static ResourceMetadata createForTest() {
        return ResourceMetadata.builder()
                .format("test-format")
                .size(1000L)
                .duration("00:00:00")
                .resolution("1920x1080")
                .additionalInfo("test-info")
                .build();
    }

    public void validate() {
        if (size != null && size <= 0) {
            throw new IllegalArgumentException("파일 크기는 0보다 커야 합니다.");
        }
    }
}