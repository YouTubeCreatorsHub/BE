package com.creatorhub.application.resource.dto;

import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ResourceResponse {
    private final UUID id;
    private final String name;
    private final ResourceType type;
    private final String url;
    private final String thumbnailUrl;
    private final LicenseType licenseType;
    private final ResourceMetadata metadata;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}