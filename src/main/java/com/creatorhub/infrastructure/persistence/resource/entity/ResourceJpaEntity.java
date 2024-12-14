package com.creatorhub.infrastructure.persistence.resource.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import com.creatorhub.domain.resource.vo.LicenseType;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.domain.resource.vo.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "resources")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    private String url;
    @Column(name = "thumbnail_url")  // 컬럼명 명시적 지정
    private String thumbnailUrl;  // 썸네일 URL 필드 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseType licenseType;

    @Embedded
    private ResourceMetadata metadata;
}