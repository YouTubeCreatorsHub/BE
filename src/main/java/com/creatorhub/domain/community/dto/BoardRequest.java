package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardRequest {
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    private String description;

    @Builder.Default
    private boolean isEnabled = true;

    public Board toEntity() {
        return Board.builder()
                .name(name)
                .description(description)
                .isEnabled(isEnabled)
                .build();
    }
}