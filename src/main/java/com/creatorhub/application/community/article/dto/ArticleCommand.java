package com.creatorhub.application.community.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class ArticleCommand {

    @Getter
    @Builder
    public static class Create {
        @NotBlank(message = "제목은 필수입니다")
        @Size(min = 1, max = 100, message = "제목은 1-100자 사이여야 합니다")
        private String title;

        @NotBlank(message = "내용은 필수입니다")
        @Size(min = 1, max = 10000, message = "내용은 1-10000자 사이여야 합니다")
        private String content;

        @NotNull(message = "게시판은 필수입니다")
        private UUID boardId;

        private UUID categoryId;
    }

    @Getter
    @Builder
    public static class Update {
        private UUID id;

        @Size(min = 1, max = 100, message = "제목은 1-100자 사이여야 합니다")
        private String title;

        @Size(min = 1, max = 10000, message = "내용은 1-10000자 사이여야 합니다")
        private String content;

        private UUID categoryId;
    }
}