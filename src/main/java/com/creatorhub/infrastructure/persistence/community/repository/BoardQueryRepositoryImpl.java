package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.BoardSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QBoardJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardJpaEntity> search(BoardSearchCondition condition, Pageable pageable) {
        QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 검색 (게시판 이름, 설명)
        if (condition.getSearchKeyword() != null) {
            builder.and(board.name.containsIgnoreCase(condition.getSearchKeyword())
                    .or(board.description.containsIgnoreCase(condition.getSearchKeyword())));
        }

        // 활성화 상태 검색
        if (condition.getIsEnabled() != null) {
            builder.and(board.isEnabled.eq(condition.getIsEnabled()));
        }

        // 기간 검색
        if (condition.getStartDate() != null) {
            builder.and(board.createdAt.goe(condition.getStartDate()));
        }
        if (condition.getEndDate() != null) {
            builder.and(board.createdAt.loe(condition.getEndDate()));
        }

        // 게시글 수 범위 검색
        if (condition.getMinArticleCount() != null) {
            builder.and(board.articles.size().goe(condition.getMinArticleCount()));
        }
        if (condition.getMaxArticleCount() != null) {
            builder.and(board.articles.size().loe(condition.getMaxArticleCount()));
        }

        // 카테고리 관련 검색
        if (condition.getCategoryId() != null) {
            builder.and(board.categories.any().id.eq(condition.getCategoryId()));
        }
        if (condition.getHasCategories() != null) {
            if (condition.getHasCategories()) {
                builder.and(board.categories.isNotEmpty());
            } else {
                builder.and(board.categories.isEmpty());
            }
        }

        JPAQuery<BoardJpaEntity> query = queryFactory
                .selectFrom(board)
                .leftJoin(board.categories).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 조건 적용
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                switch (order.getProperty()) {
                    case "name" -> query.orderBy(order.isAscending() ? board.name.asc() : board.name.desc());
                    case "createdAt" -> query.orderBy(order.isAscending() ? board.createdAt.asc() : board.createdAt.desc());
                    case "articleCount" -> query.orderBy(order.isAscending() ? board.articles.size().asc() : board.articles.size().desc());
                }
            });
        } else {
            query.orderBy(board.createdAt.desc());
        }

        List<BoardJpaEntity> results = query.fetch();

        long total = queryFactory
                .select(board.count())
                .from(board)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}