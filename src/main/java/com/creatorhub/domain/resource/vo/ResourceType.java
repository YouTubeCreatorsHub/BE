package com.creatorhub.domain.resource.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceType {
    MUSIC("음악", "audio", "audio/*"),
    IMAGE("이미지", "image", "image/*"),
    VIDEO("비디오", "video", "video/*"),
    TEMPLATE("템플릿", "template", "application/json"),
    FONT("폰트", "font", "font/*");

    private final String displayName;
    private final String category;
    private final String mimeType;
}
