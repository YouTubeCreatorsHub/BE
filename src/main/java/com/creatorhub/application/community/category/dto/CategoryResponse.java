package com.creatorhub.application.community.category.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {
    private final UUID id;
    private final String name;
    private final UUID boardId;
    private final String boardName;
    private final boolean isEnabled;
    private final long articleCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
}