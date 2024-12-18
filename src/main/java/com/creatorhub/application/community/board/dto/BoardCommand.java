package com.creatorhub.application.community.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

public class BoardCommand {

    @Getter
    @Builder
    public static class Create {
        @NotBlank(message = "게시판 이름은 필수입니다")
        @Size(min = 2, max = 50, message = "게시판 이름은 2-50자 사이여야 합니다")
        private String name;

        @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
        private String description;

        @Builder.Default
        private boolean isEnabled = true;
    }

    @Getter
    @Builder
    public static class Update {
        private UUID id;

        @Size(min = 2, max = 50, message = "게시판 이름은 2-50자 사이여야 합니다")
        private String name;

        @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
        private String description;

        private Boolean isEnabled;
    }
}