package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleRequest {
    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotNull(message = "게시판은 필수입니다")
    private UUID boardId;

    private UUID categoryId;

    public Article toEntity() {
        return Article.builder()
                .title(title)
                .content(content)
                .board(Board.builder().id(boardId).build())
                .category(categoryId != null ? Category.builder().id(categoryId).build() : null)
                .build();
    }
}