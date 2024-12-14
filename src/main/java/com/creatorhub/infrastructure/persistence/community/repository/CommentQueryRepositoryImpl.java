package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.CommentSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.CommentJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QCommentJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentJpaEntity> search(CommentSearchCondition condition, Pageable pageable) {
        QCommentJpaEntity comment = QCommentJpaEntity.commentJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 내용 검색
        if (condition.getContent() != null) {
            builder.and(comment.content.containsIgnoreCase(condition.getContent()));
        }

        // 게시글 ID로 검색
        if (condition.getArticleId() != null) {
            builder.and(comment.article.id.eq(condition.getArticleId()));
        }

        // 작성자 ID로 검색
        if (condition.getMemberId() != null) {
            builder.and(comment.member.id.eq(condition.getMemberId()));
        }

        // 기간 검색
        BooleanExpression dateCondition = createDateCondition(condition, comment);
        if (dateCondition != null) {
            builder.and(dateCondition);
        }

        // 삭제 여부
        if (condition.getIsDeleted() != null) {
            builder.and(comment.deleted.eq(condition.getIsDeleted()));
        }

        // OrderSpecifier 생성
        List<OrderSpecifier<?>> orders = createOrderSpecifier(pageable, comment);

        List<CommentJpaEntity> results = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.article).fetchJoin()
                .leftJoin(comment.member).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .fetch();

        long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression createDateCondition(CommentSearchCondition condition,
                                                  QCommentJpaEntity comment) {
        if (condition.getStartDate() != null && condition.getEndDate() != null) {
            return comment.createdAt.between(condition.getStartDate(), condition.getEndDate());
        } else if (condition.getStartDate() != null) {
            return comment.createdAt.goe(condition.getStartDate());
        } else if (condition.getEndDate() != null) {
            return comment.createdAt.loe(condition.getEndDate());
        }
        return null;
    }

    private List<OrderSpecifier<?>> createOrderSpecifier(Pageable pageable,
                                                         QCommentJpaEntity comment) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            orders.add(comment.createdAt.desc());
        } else {
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("createdAt")) {
                    OrderSpecifier<?> orderSpecifier = order.isAscending() ?
                            comment.createdAt.asc() : comment.createdAt.desc();
                    orders.add(orderSpecifier);
                }
            });
        }

        return orders;
    }
}