package com.creatorhub.application.resource.dto;

import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateResourceCommand {
    private String name;
    private ResourceType type;
    private LicenseType licenseType;
    private ResourceMetadata metadata;

    public static UpdateResourceCommand of(String name, ResourceType type,
                                           LicenseType licenseType, ResourceMetadata metadata) {
        return new UpdateResourceCommand(name, type, licenseType, metadata);
    }
}