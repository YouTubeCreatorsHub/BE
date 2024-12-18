package com.creatorhub.infrastructure.persistence.community.adapter;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.repository.ArticleRepository;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QBoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QCategoryJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QMemberJpaEntity;
import com.creatorhub.infrastructure.persistence.community.mapper.ArticleJpaMapper;
import com.creatorhub.infrastructure.persistence.community.repository.ArticleJpaRepository;
import com.creatorhub.domain.community.repository.search.ArticleSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArticlePersistenceAdapter implements ArticleRepository {

    private final ArticleJpaRepository articleJpaRepository;
    private final ArticleJpaMapper articleJpaMapper;
    private final JPAQueryFactory queryFactory;

    // QueryDSL 엔티티
    private final QArticleJpaEntity article = QArticleJpaEntity.articleJpaEntity;
    private final QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;
    private final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;
    private final QMemberJpaEntity member = QMemberJpaEntity.memberJpaEntity;

    // 정렬 필드 매핑
    private final Map<String, Path<?>> sortFieldMap = Map.of(
            "createdAt", article.createdAt,
            "updatedAt", article.updatedAt,
            "title", article.title,
            "viewCount", article.viewCount
    );

    @Override
    public Article save(Article article) {
        ArticleJpaEntity savedEntity = articleJpaRepository.save(articleJpaMapper.toJpaEntity(article));
        return articleJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Article> findById(UUID id) {
        ArticleJpaEntity entity = queryFactory
                .selectFrom(article)
                .leftJoin(article.board, board).fetchJoin()
                .leftJoin(article.category, category).fetchJoin()
                .leftJoin(article.member, member).fetchJoin()
                .where(
                        article.id.eq(id),
                        article.deleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(entity).map(articleJpaMapper::toDomain);
    }

    @Override
    public Page<Article> findAll(Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<ArticleJpaEntity> content = queryFactory
                .selectFrom(article)
                .leftJoin(article.board, board).fetchJoin()
                .leftJoin(article.category, category).fetchJoin()
                .leftJoin(article.member, member).fetchJoin()
                .where(article.deleted.isFalse())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(article.count())
                .from(article)
                .where(article.deleted.isFalse());

        return PageableExecutionUtils.getPage(
                content.stream().map(articleJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Page<Article> findAllByBoardId(UUID boardId, Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<ArticleJpaEntity> content = queryFactory
                .selectFrom(article)
                .leftJoin(article.board, board).fetchJoin()
                .leftJoin(article.category, category).fetchJoin()
                .leftJoin(article.member, member).fetchJoin()
                .where(
                        article.board.id.eq(boardId),
                        article.deleted.isFalse()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        article.board.id.eq(boardId),
                        article.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(
                content.stream().map(articleJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Page<Article> findByBoardIdAndCategoryId(UUID boardId, UUID categoryId, Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<ArticleJpaEntity> content = queryFactory
                .selectFrom(article)
                .leftJoin(article.board, board).fetchJoin()
                .leftJoin(article.category, category).fetchJoin()
                .leftJoin(article.member, member).fetchJoin()
                .where(
                        article.board.id.eq(boardId),
                        article.category.id.eq(categoryId),
                        article.deleted.isFalse()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        article.board.id.eq(boardId),
                        article.category.id.eq(categoryId),
                        article.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(
                content.stream().map(articleJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public List<Article> findByMemberId(UUID memberId) {
        List<ArticleJpaEntity> entities = queryFactory
                .selectFrom(article)
                .leftJoin(article.board, board).fetchJoin()
                .leftJoin(article.category, category).fetchJoin()
                .leftJoin(article.member, member).fetchJoin()
                .where(
                        article.member.id.eq(memberId),
                        article.deleted.isFalse()
                )
                .orderBy(article.createdAt.desc())
                .fetch();

        return entities.stream()
                .map(articleJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(article)
                .where(
                        article.id.eq(id),
                        article.deleted.isFalse()
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public void deleteById(UUID id) {
        queryFactory
                .update(article)
                .set(article.deleted, true)
                .where(article.id.eq(id))
                .execute();
    }

    @Override
    public long countByBoardId(UUID boardId) {
        return queryFactory
                .select(article.count())
                .from(article)
                .where(
                        article.board.id.eq(boardId),
                        article.deleted.isFalse()
                )
                .fetchOne();
    }

    @Override
    public Page<Article> search(ArticleSearchCondition condition, Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<ArticleJpaEntity> content = queryFactory
                .selectFrom(article)
                .leftJoin(article.board, board).fetchJoin()
                .leftJoin(article.category, category).fetchJoin()
                .leftJoin(article.member, member).fetchJoin()
                .where(
                        searchKeywordContains(condition.getSearchKeyword()),
                        boardIdEq(condition.getBoardId()),
                        categoryIdEq(condition.getCategoryId()),
                        memberIdEq(condition.getMemberId()),
                        article.deleted.isFalse()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        searchKeywordContains(condition.getSearchKeyword()),
                        boardIdEq(condition.getBoardId()),
                        categoryIdEq(condition.getCategoryId()),
                        memberIdEq(condition.getMemberId()),
                        article.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(
                content.stream().map(articleJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    private BooleanExpression searchKeywordContains(String searchKeyword) {
        return StringUtils.hasText(searchKeyword) ?
                article.title.containsIgnoreCase(searchKeyword)
                        .or(article.content.containsIgnoreCase(searchKeyword)) :
                null;
    }

    private BooleanExpression boardIdEq(UUID boardId) {
        return boardId != null ? article.board.id.eq(boardId) : null;
    }

    private BooleanExpression categoryIdEq(UUID categoryId) {
        return categoryId != null ? article.category.id.eq(categoryId) : null;
    }

    private BooleanExpression memberIdEq(UUID memberId) {
        return memberId != null ? article.member.id.eq(memberId) : null;
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        sort.forEach(order -> {
            Path<?> path = sortFieldMap.get(order.getProperty());
            if (path != null) {
                orders.add(new OrderSpecifier(
                        order.isAscending() ?
                                com.querydsl.core.types.Order.ASC :
                                com.querydsl.core.types.Order.DESC,
                        path
                ));
            }
        });

        // 기본 정렬: 생성일시 내림차순
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(
                    com.querydsl.core.types.Order.DESC,
                    article.createdAt
            ));
        }

        return orders;
    }
}