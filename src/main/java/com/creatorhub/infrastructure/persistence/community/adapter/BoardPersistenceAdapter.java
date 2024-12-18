package com.creatorhub.infrastructure.persistence.community.adapter;

import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.repository.BoardRepository;
import com.creatorhub.domain.community.repository.search.BoardSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QBoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QCategoryJpaEntity;
import com.creatorhub.infrastructure.persistence.community.mapper.BoardJpaMapper;
import com.creatorhub.infrastructure.persistence.community.repository.BoardJpaRepository;
import com.querydsl.core.BooleanBuilder;
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

import java.util.*;

@Component
@RequiredArgsConstructor
public class BoardPersistenceAdapter implements BoardRepository {

    private final BoardJpaRepository boardJpaRepository;
    private final BoardJpaMapper boardJpaMapper;
    private final JPAQueryFactory queryFactory;

    private final QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;
    private final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;
    private final QArticleJpaEntity article = QArticleJpaEntity.articleJpaEntity;

    private final Map<String, Path<?>> sortFieldMap = Map.of(
            "createdAt", board.createdAt,
            "updatedAt", board.updatedAt,
            "name", board.name
    );

    @Override
    public Board save(Board domainBoard) {
        BoardJpaEntity savedEntity = boardJpaRepository.save(boardJpaMapper.toJpaEntity(domainBoard));
        return boardJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Board> findById(UUID id) {
        BoardJpaEntity entity = queryFactory
                .selectFrom(board)
                .where(
                        board.id.eq(id),
                        board.deleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(entity)
                .map(boardJpaMapper::toDomain);
    }

    @Override
    public Optional<Board> findByIdWithCategories(UUID id) {
        BoardJpaEntity entity = queryFactory
                .selectFrom(board)
                .leftJoin(board.categories, category).fetchJoin()
                .where(
                        board.id.eq(id),
                        board.deleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(entity)
                .map(boardJpaMapper::toDomain);
    }

    @Override
    public Page<Board> findAll(Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<BoardJpaEntity> content = queryFactory
                .selectFrom(board)
                .leftJoin(board.categories, category).fetchJoin()
                .where(board.deleted.isFalse())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.countDistinct())
                .from(board)
                .where(board.deleted.isFalse());

        return PageableExecutionUtils.getPage(
                content.stream().map(boardJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Page<Board> search(BoardSearchCondition condition, Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.deleted.isFalse());

        if (StringUtils.hasText(condition.getSearchKeyword())) {
            builder.and(
                    board.name.containsIgnoreCase(condition.getSearchKeyword())
                            .or(board.description.containsIgnoreCase(condition.getSearchKeyword()))
            );
        }

        if (condition.getIsEnabled() != null) {
            builder.and(board.isEnabled.eq(condition.getIsEnabled()));
        }

        if (condition.getCategoryId() != null) {
            builder.and(category.id.eq(condition.getCategoryId()));
        }

        if (condition.getHasCategories() != null) {
            if (condition.getHasCategories()) {
                builder.and(board.categories.isNotEmpty());
            } else {
                builder.and(board.categories.isEmpty());
            }
        }

        List<BoardJpaEntity> content = queryFactory
                .selectFrom(board)
                .leftJoin(board.categories, category).fetchJoin()
                .where(builder)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.countDistinct())
                .from(board)
                .leftJoin(board.categories, category)
                .where(builder);

        return PageableExecutionUtils.getPage(
                content.stream().map(boardJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public List<Board> findAllWithCategories() {
        List<BoardJpaEntity> entities = queryFactory
                .selectFrom(board)
                .leftJoin(board.categories, category).fetchJoin()
                .where(board.deleted.isFalse())
                .orderBy(board.createdAt.desc())
                .distinct()
                .fetch();

        return entities.stream()
                .map(boardJpaMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        queryFactory
                .update(board)
                .set(board.deleted, true)
                .where(board.id.eq(id))
                .execute();
    }

    @Override
    public boolean existsByName(String name) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(board)
                .where(
                        board.name.eq(name),
                        board.deleted.isFalse()
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Page<Board> findByIsEnabledTrue(Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<BoardJpaEntity> content = queryFactory
                .selectFrom(board)
                .leftJoin(board.categories, category).fetchJoin()
                .where(
                        board.isEnabled.isTrue(),
                        board.deleted.isFalse()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.countDistinct())
                .from(board)
                .where(
                        board.isEnabled.isTrue(),
                        board.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(
                content.stream().map(boardJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public long countByIsEnabled(boolean isEnabled) {
        return queryFactory
                .select(board.count())
                .from(board)
                .where(
                        board.isEnabled.eq(isEnabled),
                        board.deleted.isFalse()
                )
                .fetchOne();
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

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(
                    com.querydsl.core.types.Order.DESC,
                    board.createdAt
            ));
        }

        return orders;
    }
}