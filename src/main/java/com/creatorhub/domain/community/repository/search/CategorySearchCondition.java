package com.creatorhub.domain.community.repository.search;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CategorySearchCondition {
    private String searchKeyword;          // 카테고리 이름 검색
    private UUID boardId;                  // 게시판 ID
    private Boolean isEnabled;             // 활성화 상태
    private LocalDateTime startDate;       // 검색 시작일
    private LocalDateTime endDate;         // 검색 종료일
    private Integer minArticleCount;       // 최소 게시글 수
    private Integer maxArticleCount;       // 최대 게시글 수
    private Boolean hasArticles;           // 게시글 존재 여부

    public static CategorySearchCondition onlyEnabled() {
        return CategorySearchCondition.builder()
                .isEnabled(true)
                .build();
    }

    public static CategorySearchCondition byBoard(UUID boardId) {
        return CategorySearchCondition.builder()
                .boardId(boardId)
                .isEnabled(true)
                .build();
    }
}