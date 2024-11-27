package com.creatorhub.presentation.resource.dto;

import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateResourceRequest {
    @NotBlank(message = "리소스 이름은 필수입니다.")
    private String name;

    @NotNull(message = "리소스 타입은 필수입니다.")
    private ResourceType type;

    @NotNull(message = "라이센스 타입은 필수입니다.")
    private LicenseType licenseType;

    private ResourceMetadata metadata;
}