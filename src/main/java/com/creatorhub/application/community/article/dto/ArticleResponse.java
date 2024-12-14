package com.creatorhub.application.community.article.dto;

import com.creatorhub.domain.community.entity.Article;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ArticleResponse {
    private final UUID id;
    private final String title;
    private final String content;
    private final int viewCount;
    private final UUID boardId;
    private final String boardName;
    private final UUID categoryId;
    private final String categoryName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;

    public static ArticleResponse from(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .viewCount(article.getViewCount())
                .boardId(article.getBoard().getId())
                .boardName(article.getBoard().getName())
                .categoryId(article.getCategory() != null ? article.getCategory().getId() : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .createdBy(article.getCreatedBy())
                .build();
    }
}