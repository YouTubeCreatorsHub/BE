package com.creatorhub.domain.community.repository.search;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ArticleSearchCondition {
    private String searchKeyword;      // 검색어 (제목, 내용)
    private UUID boardId;              // 게시판 ID
    private UUID categoryId;           // 카테고리 ID
    private UUID memberId;             // 작성자 ID
    private LocalDateTime startDate;   // 검색 시작일
    private LocalDateTime endDate;     // 검색 종료일
    private Boolean isDeleted;         // 삭제 여부

    @Builder.Default
    private SearchOperator searchOperator = SearchOperator.AND; // 검색 조건 연산자

    public enum SearchOperator {
        AND,    // 모든 조건을 만족
        OR      // 하나라도 만족
    }

    public static ArticleSearchCondition of(String searchKeyword) {
        return ArticleSearchCondition.builder()
                .searchKeyword(searchKeyword)
                .build();
    }

    public static ArticleSearchCondition of(UUID boardId, UUID categoryId) {
        return ArticleSearchCondition.builder()
                .boardId(boardId)
                .categoryId(categoryId)
                .build();
    }

    public static ArticleSearchCondition bymember(UUID memberId) {
        return ArticleSearchCondition.builder()
                .memberId(memberId)
                .build();
    }

    public static ArticleSearchCondition byDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return ArticleSearchCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public ArticleSearchCondition withSearchOperator(SearchOperator operator) {
        return ArticleSearchCondition.builder()
                .searchKeyword(this.searchKeyword)
                .boardId(this.boardId)
                .categoryId(this.categoryId)
                .memberId(this.memberId)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isDeleted(this.isDeleted)
                .searchOperator(operator)
                .build();
    }
}