package com.creatorhub.infrastructure.persistence.community.adapter;

import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.CategoryRepository;
import com.creatorhub.domain.community.repository.search.CategorySearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QBoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QCategoryJpaEntity;
import com.creatorhub.infrastructure.persistence.community.mapper.CategoryJpaMapper;
import com.creatorhub.infrastructure.persistence.community.repository.CategoryJpaRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
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
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryJpaMapper categoryJpaMapper;
    private final JPAQueryFactory queryFactory;

    private final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;
    private final QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;
    private final QArticleJpaEntity article = QArticleJpaEntity.articleJpaEntity;

    private final Map<String, Path<?>> sortFieldMap = Map.of(
            "createdAt", category.createdAt,
            "updatedAt", category.updatedAt,
            "name", category.name
    );

    @Override
    public Category save(Category domainCategory) {
        CategoryJpaEntity savedEntity = categoryJpaRepository.save(categoryJpaMapper.toJpaEntity(domainCategory));
        return categoryJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        CategoryJpaEntity entity = queryFactory
                .selectFrom(category)
                .leftJoin(category.board, board).fetchJoin()
                .where(
                        category.id.eq(id),
                        category.deleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(entity)
                .map(categoryJpaMapper::toDomain);
    }

    @Override
    public Optional<Category> findByIdWithArticles(UUID id) {
        CategoryJpaEntity entity = queryFactory
                .selectFrom(category)
                .leftJoin(category.board, board).fetchJoin()
                .leftJoin(category.articles, article).fetchJoin()
                .where(
                        category.id.eq(id),
                        category.deleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(entity)
                .map(categoryJpaMapper::toDomain);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<CategoryJpaEntity> content = queryFactory
                .selectFrom(category)
                .leftJoin(category.board, board).fetchJoin()
                .where(category.deleted.isFalse())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(category.count())
                .from(category)
                .where(category.deleted.isFalse());

        return PageableExecutionUtils.getPage(
                content.stream().map(categoryJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Page<Category> search(CategorySearchCondition condition, Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(category.deleted.isFalse());

        if (StringUtils.hasText(condition.getSearchKeyword())) {
            builder.and(category.name.containsIgnoreCase(condition.getSearchKeyword()));
        }

        if (condition.getBoardId() != null) {
            builder.and(category.board.id.eq(condition.getBoardId()));
        }

        if (condition.getIsEnabled() != null) {
            builder.and(category.isEnabled.eq(condition.getIsEnabled()));
        }

        if (condition.getHasArticles() != null) {
            if (condition.getHasArticles()) {
                builder.and(category.articles.isNotEmpty());
            } else {
                builder.and(category.articles.isEmpty());
            }
        }

        List<CategoryJpaEntity> content = queryFactory
                .selectFrom(category)
                .leftJoin(category.board, board).fetchJoin()
                .where(builder)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(category.count())
                .from(category)
                .where(builder);

        return PageableExecutionUtils.getPage(
                content.stream().map(categoryJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public List<Category> findAllByBoardId(UUID boardId) {
        List<CategoryJpaEntity> entities = queryFactory
                .selectFrom(category)
                .leftJoin(category.board, board).fetchJoin()
                .where(
                        category.board.id.eq(boardId),
                        category.deleted.isFalse()
                )
                .orderBy(category.createdAt.desc())
                .fetch();

        return entities.stream()
                .map(categoryJpaMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        queryFactory
                .update(category)
                .set(category.deleted, true)
                .where(category.id.eq(id))
                .execute();
    }

    @Override
    public boolean existsByNameAndBoardId(String name, UUID boardId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(category)
                .where(
                        category.name.eq(name),
                        category.board.id.eq(boardId),
                        category.deleted.isFalse()
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Page<Category> findByBoardIdAndIsEnabledTrue(UUID boardId, Pageable pageable) {
        List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable.getSort());

        List<CategoryJpaEntity> content = queryFactory
                .selectFrom(category)
                .leftJoin(category.board, board).fetchJoin()
                .where(
                        category.board.id.eq(boardId),
                        category.isEnabled.isTrue(),
                        category.deleted.isFalse()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(category.count())
                .from(category)
                .where(
                        category.board.id.eq(boardId),
                        category.isEnabled.isTrue(),
                        category.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(
                content.stream().map(categoryJpaMapper::toDomain).toList(),
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public long countByBoardId(UUID boardId) {
        return queryFactory
                .select(category.count())
                .from(category)
                .where(
                        category.board.id.eq(boardId),
                        category.deleted.isFalse()
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
                    category.createdAt
            ));
        }

        return orders;
    }
}