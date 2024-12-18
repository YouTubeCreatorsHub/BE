package com.creatorhub.domain.community.repository.search;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BoardSearchCondition {
    private String searchKeyword;      // 게시판 이름, 설명 검색
    private Boolean isEnabled;         // 활성화 상태
    private LocalDateTime startDate;   // 검색 시작일
    private LocalDateTime endDate;     // 검색 종료일
    private Integer minArticleCount;   // 최소 게시글 수
    private Integer maxArticleCount;   // 최대 게시글 수
    private UUID categoryId;           // 특정 카테고리가 있는 게시판 검색
    private Boolean hasCategories;     // 카테고리 존재 여부

    public static BoardSearchCondition onlyEnabled() {
        return BoardSearchCondition.builder()
                .isEnabled(true)
                .build();
    }

    public static BoardSearchCondition withKeyword(String keyword) {
        return BoardSearchCondition.builder()
                .searchKeyword(keyword)
                .isEnabled(true)
                .build();
    }
}