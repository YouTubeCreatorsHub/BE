package com.creatorhub.domain.resource.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceEntity extends BaseEntity {

    private String name;
    private ResourceType type;
    private String url;
    private String thumbnailUrl;  // 썸네일 URL 필드 추가
    private LicenseType licenseType;
    private ResourceMetadata metadata;

    public void updateUrl(String url) {
        this.url = url;
    }

    public void updateThumbnailUrl(String thumbnailUrl) {  // 썸네일 URL 업데이트 메서드 추가
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateMetadata(ResourceMetadata metadata) {
        if (metadata == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "메타데이터는 null일 수 없습니다.");
        }
        metadata.validate();
        this.metadata = metadata;
    }

    public void update(String name, ResourceType type, LicenseType licenseType) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (type != null) {
            this.type = type;
        }
        if (licenseType != null) {
            this.licenseType = licenseType;
        }
    }

    public void delete(String deleteBy) {
        super.onDelete(deleteBy);
    }
}