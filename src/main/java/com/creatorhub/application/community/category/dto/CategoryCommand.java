package com.creatorhub.application.community.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

public class CategoryCommand {

    @Getter
    @Builder
    public static class Create {
        @NotBlank(message = "카테고리 이름은 필수입니다")
        @Size(min = 2, max = 30, message = "카테고리 이름은 2-30자 사이여야 합니다")
        private String name;

        @NotNull(message = "게시판은 필수입니다")
        private UUID boardId;

        @Builder.Default
        private boolean isEnabled = true;
    }

    @Getter
    @Builder
    public static class Update {
        private UUID id;

        @Size(min = 2, max = 30, message = "카테고리 이름은 2-30자 사이여야 합니다")
        private String name;

        private Boolean isEnabled;

        public void setId(UUID id) {
            this.id = id;
        }
    }
}