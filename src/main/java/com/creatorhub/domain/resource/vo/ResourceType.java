package com.creatorhub.domain.resource.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ResourceType {
    MUSIC("음악", "audio", Arrays.asList("audio/mpeg", "audio/wav", "audio/ogg")),
    IMAGE("이미지", "image", Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp")),
    VIDEO("비디오", "video", Arrays.asList("video/mp4", "video/webm")),
    TEMPLATE("템플릿", "template", Arrays.asList("application/json")),
    FONT("폰트", "font", Arrays.asList("font/ttf", "font/otf", "font/woff", "font/woff2"));

    private final String displayName;
    private final String category;
    private final List<String> supportedMimeTypes;

    public static List<String> getSupportedMimeTypes() {
        return Arrays.stream(values())
                .flatMap(type -> type.supportedMimeTypes.stream())
                .collect(Collectors.toList());
    }
}
