package com.creatorhub.application.resource.dto;

import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ResourceSearchCriteria {
    private String name;
    private ResourceType type;
    private LicenseType licenseType;
    private Long minSize;
    private Long maxSize;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String formatType;
}