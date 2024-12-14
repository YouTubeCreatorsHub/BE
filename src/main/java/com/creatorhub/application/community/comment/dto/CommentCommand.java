package com.creatorhub.application.community.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

public class CommentCommand {

    @Getter
    @Builder
    public static class Create {
        @NotBlank(message = "댓글 내용은 필수입니다")
        @Size(min = 1, max = 500, message = "댓글 내용은 1-500자 사이여야 합니다")
        private String content;

        @NotNull(message = "게시글 ID는 필수입니다")
        private UUID articleId;

        @NotNull(message = "작성자 ID는 필수입니다")
        private UUID memberId;
    }

    @Getter
    @Builder
    public static class Update {
        private UUID id;

        @NotBlank(message = "댓글 내용은 필수입니다")
        @Size(min = 1, max = 500, message = "댓글 내용은 1-500자 사이여야 합니다")
        private String content;
    }
}