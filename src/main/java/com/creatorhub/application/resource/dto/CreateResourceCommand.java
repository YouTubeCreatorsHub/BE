package com.creatorhub.application.resource.dto;

import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateResourceCommand {
    private String name;
    private ResourceType type;
    private LicenseType licenseType;
    private ResourceMetadata metadata;
    private MultipartFile file;

    public static CreateResourceCommand of(String name, ResourceType type, LicenseType licenseType,
                                           ResourceMetadata metadata, MultipartFile file) {
        return new CreateResourceCommand(name, type, licenseType, metadata, file);
    }
}