package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "게시판은 필수입니다")
    private UUID boardId;

    @Builder.Default
    private boolean isEnabled = true;

    public Category toEntity() {
        return Category.builder()
                .name(name)
                .board(Board.builder().id(boardId).build())
                .isEnabled(isEnabled)
                .build();
    }
}