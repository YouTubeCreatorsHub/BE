package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.ArticleSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QArticleJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ArticleJpaEntity> search(ArticleSearchCondition condition, Pageable pageable) {
        QArticleJpaEntity article = QArticleJpaEntity.articleJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 검색 (제목, 내용)
        if (condition.getSearchKeyword() != null) {
            if (condition.getSearchOperator() == ArticleSearchCondition.SearchOperator.AND) {
                builder.and(article.title.containsIgnoreCase(condition.getSearchKeyword())
                        .and(article.content.containsIgnoreCase(condition.getSearchKeyword())));
            } else {
                builder.and(article.title.containsIgnoreCase(condition.getSearchKeyword())
                        .or(article.content.containsIgnoreCase(condition.getSearchKeyword())));
            }
        }

        // 게시판 검색
        if (condition.getBoardId() != null) {
            builder.and(article.board.id.eq(condition.getBoardId()));
        }

        // 카테고리 검색
        if (condition.getCategoryId() != null) {
            builder.and(article.category.id.eq(condition.getCategoryId()));
        }

        // 작성자 검색
        if (condition.getMemberId() != null) {
            builder.and(article.member.id.eq(condition.getMemberId()));
        }

        // 기간 검색
        builder.and(createDateCondition(condition, article));

        // 삭제 여부
        if (condition.getIsDeleted() != null) {
            builder.and(article.deleted.eq(condition.getIsDeleted()));
        }

        List<ArticleJpaEntity> results = queryFactory
                .selectFrom(article)
                .leftJoin(article.board).fetchJoin()
                .leftJoin(article.category).fetchJoin()
                .leftJoin(article.member).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(article.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(article.count())
                .from(article)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression createDateCondition(ArticleSearchCondition condition,
                                                  QArticleJpaEntity article) {
        if (condition.getStartDate() != null && condition.getEndDate() != null) {
            return article.createdAt.between(condition.getStartDate(), condition.getEndDate());
        } else if (condition.getStartDate() != null) {
            return article.createdAt.goe(condition.getStartDate());
        } else if (condition.getEndDate() != null) {
            return article.createdAt.loe(condition.getEndDate());
        }
        return null;
    }
}