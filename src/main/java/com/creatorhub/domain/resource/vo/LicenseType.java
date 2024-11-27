package com.creatorhub.domain.resource.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LicenseType {
    FREE("무료", 0),
    PREMIUM("프리미엄", 1),
    ENTERPRISE("기업용", 2);

    private final String displayName;
    private final int level;
}