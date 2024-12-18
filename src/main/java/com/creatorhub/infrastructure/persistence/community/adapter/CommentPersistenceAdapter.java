package com.creatorhub.infrastructure.persistence.community.adapter;

import com.creatorhub.domain.community.entity.Comment;
import com.creatorhub.domain.community.repository.CommentRepository;
import com.creatorhub.domain.community.repository.search.CommentSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.CommentJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QCommentJpaEntity;
import com.creatorhub.infrastructure.persistence.community.mapper.CommentJpaMapper;
import com.creatorhub.infrastructure.persistence.community.repository.CommentJpaRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentPersistenceAdapter implements CommentRepository {
    private final JPAQueryFactory queryFactory;
    private final CommentJpaMapper commentJpaMapper;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        return commentJpaMapper.toDomain(
                commentJpaRepository.save(
                        commentJpaMapper.toJpaEntity(comment)
                )
        );
    }

    @Override
    public Optional<Comment> findById(UUID id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QCommentJpaEntity.commentJpaEntity)
                        .leftJoin(QCommentJpaEntity.commentJpaEntity.article).fetchJoin()
                        .leftJoin(QCommentJpaEntity.commentJpaEntity.member).fetchJoin()
                        .where(QCommentJpaEntity.commentJpaEntity.id.eq(id))
                        .fetchOne()
        ).map(commentJpaMapper::toDomain);
    }

    @Override
    public List<Comment> findAllByArticleId(UUID articleId) {
        return queryFactory
                .selectFrom(QCommentJpaEntity.commentJpaEntity)
                .leftJoin(QCommentJpaEntity.commentJpaEntity.member).fetchJoin()
                .where(QCommentJpaEntity.commentJpaEntity.article.id.eq(articleId))
                .fetch()
                .stream()
                .map(commentJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        commentJpaRepository.deleteById(id);
    }

    @Override
    public long countByArticleId(UUID articleId) {
        return queryFactory
                .selectFrom(QCommentJpaEntity.commentJpaEntity)
                .where(QCommentJpaEntity.commentJpaEntity.article.id.eq(articleId))
                .fetchCount();
    }

    public Page<Comment> search(CommentSearchCondition condition, Pageable pageable) {
        QCommentJpaEntity comment = QCommentJpaEntity.commentJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 내용 검색
        if (condition.getContent() != null) {
            builder.and(comment.content.containsIgnoreCase(condition.getContent()));
        }

        // 게시글 검색
        if (condition.getArticleId() != null) {
            builder.and(comment.article.id.eq(condition.getArticleId()));
        }

        // 작성자 검색
        if (condition.getMemberId() != null) {
            builder.and(comment.member.id.eq(condition.getMemberId()));
        }

        // 기간 검색
        if (condition.getStartDate() != null) {
            builder.and(comment.createdAt.goe(condition.getStartDate()));
        }
        if (condition.getEndDate() != null) {
            builder.and(comment.createdAt.loe(condition.getEndDate()));
        }

        // 삭제 여부
        if (condition.getIsDeleted() != null) {
            builder.and(comment.deleted.eq(condition.getIsDeleted()));
        }

        JPAQuery<CommentJpaEntity> query = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.article).fetchJoin()
                .leftJoin(comment.member).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 조건 적용
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("createdAt")) {
                    query.orderBy(order.isAscending() ? comment.createdAt.asc() : comment.createdAt.desc());
                }
            });
        } else {
            query.orderBy(comment.createdAt.desc());  // 기본 정렬: 최신순
        }

        List<Comment> comments = query.fetch().stream()
                .map(commentJpaMapper::toDomain)
                .collect(Collectors.toList());

        long total = queryFactory
                .selectFrom(comment)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(comments, pageable, total);
    }

    @Override
    public Page<Comment> findPageByArticleId(UUID articleId, Pageable pageable) {
        QCommentJpaEntity comment = QCommentJpaEntity.commentJpaEntity;

        List<CommentJpaEntity> content = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member).fetchJoin()
                .where(comment.article.id.eq(articleId),
                        comment.deleted.isFalse())
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.article.id.eq(articleId),
                        comment.deleted.isFalse());

        return PageableExecutionUtils.getPage(
                content.stream().map(commentJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }
}