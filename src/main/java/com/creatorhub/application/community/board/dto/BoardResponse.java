package com.creatorhub.application.community.board.dto;

import com.creatorhub.application.community.category.dto.CategoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class BoardResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final boolean isEnabled;
    private final long articleCount;
    private final List<CategoryResponse> categories;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
}