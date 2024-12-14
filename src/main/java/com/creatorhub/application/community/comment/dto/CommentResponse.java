package com.creatorhub.application.community.comment.dto;

import com.creatorhub.domain.community.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CommentResponse {
    private final UUID id;
    private final String content;
    private final UUID articleId;
    private final String articleTitle;
    private final UUID memberId;
    private final String memberNickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .articleId(comment.getArticle().getId())
                .articleTitle(comment.getArticle().getTitle())
                .memberId(comment.getMember().getId())
                .memberNickname(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .createdBy(comment.getCreatedBy())
                .build();
    }
}