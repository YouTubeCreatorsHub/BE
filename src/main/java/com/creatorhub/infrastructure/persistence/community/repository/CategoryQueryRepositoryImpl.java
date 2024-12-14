package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.CategorySearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QCategoryJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CategoryJpaEntity> search(CategorySearchCondition condition, Pageable pageable) {
        QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;
        QArticleJpaEntity article = QArticleJpaEntity.articleJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 검색 (카테고리 이름)
        if (condition.getSearchKeyword() != null) {
            builder.and(category.name.containsIgnoreCase(condition.getSearchKeyword()));
        }

        // 게시판 검색
        if (condition.getBoardId() != null) {
            builder.and(category.board.id.eq(condition.getBoardId()));
        }

        // 활성화 상태 검색
        if (condition.getIsEnabled() != null) {
            builder.and(category.isEnabled.eq(condition.getIsEnabled()));
        }

        // 기간 검색
        addDateCondition(condition, category, builder);

        // 게시글 수 검색
        addArticleCountCondition(condition, category, builder);

        // 게시글 존재 여부
        if (condition.getHasArticles() != null) {
            if (condition.getHasArticles()) {
                builder.and(category.articles.isNotEmpty());
            } else {
                builder.and(category.articles.isEmpty());
            }
        }

        List<OrderSpecifier<?>> orders = createOrderSpecifiers(pageable, category);

        List<CategoryJpaEntity> results = queryFactory
                .selectFrom(category)
                .leftJoin(category.board).fetchJoin()
                .distinct()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .fetch();

        long total = queryFactory
                .select(category.countDistinct())
                .from(category)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private void addDateCondition(CategorySearchCondition condition,
                                  QCategoryJpaEntity category,
                                  BooleanBuilder builder) {
        if (condition.getStartDate() != null) {
            builder.and(category.createdAt.goe(condition.getStartDate()));
        }
        if (condition.getEndDate() != null) {
            builder.and(category.createdAt.loe(condition.getEndDate()));
        }
    }

    private void addArticleCountCondition(CategorySearchCondition condition,
                                          QCategoryJpaEntity category,
                                          BooleanBuilder builder) {
        NumberExpression<Integer> articleCount = category.articles.size();

        if (condition.getMinArticleCount() != null) {
            builder.and(articleCount.goe(condition.getMinArticleCount()));
        }
        if (condition.getMaxArticleCount() != null) {
            builder.and(articleCount.loe(condition.getMaxArticleCount()));
        }
    }

    private List<OrderSpecifier<?>> createOrderSpecifiers(Pageable pageable,
                                                          QCategoryJpaEntity category) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            orders.add(category.createdAt.desc());
        } else {
            pageable.getSort().forEach(order -> {
                switch (order.getProperty()) {
                    case "name" -> orders.add(order.isAscending() ?
                            category.name.asc() : category.name.desc());
                    case "createdAt" -> orders.add(order.isAscending() ?
                            category.createdAt.asc() : category.createdAt.desc());
                    case "articleCount" -> orders.add(order.isAscending() ?
                            category.articles.size().asc() : category.articles.size().desc());
                    default -> orders.add(category.createdAt.desc());
                }
            });
        }

        return orders;
    }
}