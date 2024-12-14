package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.MemberSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.MemberJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QMemberJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberJpaEntity> search(MemberSearchCondition condition, Pageable pageable) {
        QMemberJpaEntity member = QMemberJpaEntity.memberJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 이메일 검색 *정확성을 위해 eq사용
        if (condition.getEmail() != null) {
            builder.and(member.email.eq(condition.getEmail()));
        }

        // 닉네임 검색
        if (condition.getNickname() != null) {
            builder.and(member.nickname.containsIgnoreCase(condition.getNickname()));
        }

        // 역할 검색
        if (condition.getRole() != null) {
            builder.and(member.role.eq(condition.getRole()));
        }

        // 활성화 상태 검색
        if (condition.getIsEnabled() != null) {
            builder.and(member.isEnabled.eq(condition.getIsEnabled()));
        }

        // 기간 검색
        BooleanExpression dateCondition = createDateCondition(condition, member);
        if (dateCondition != null) {
            builder.and(dateCondition);
        }

        // 삭제 여부
        if (condition.getIsDeleted() != null) {
            builder.and(member.deleted.eq(condition.getIsDeleted()));
        }

        List<OrderSpecifier<?>> orders = createOrderSpecifier(pageable, member);

        List<MemberJpaEntity> results = queryFactory
                .selectFrom(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .fetch();

        long total = queryFactory
                .select(member.count())
                .from(member)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression createDateCondition(MemberSearchCondition condition,
                                                  QMemberJpaEntity member) {
        if (condition.getStartDate() != null && condition.getEndDate() != null) {
            return member.createdAt.between(condition.getStartDate(), condition.getEndDate());
        } else if (condition.getStartDate() != null) {
            return member.createdAt.goe(condition.getStartDate());
        } else if (condition.getEndDate() != null) {
            return member.createdAt.loe(condition.getEndDate());
        }
        return null;
    }

    private List<OrderSpecifier<?>> createOrderSpecifier(Pageable pageable,
                                                         QMemberJpaEntity member) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            orders.add(member.createdAt.desc());
        } else {
            pageable.getSort().forEach(order -> {
                switch (order.getProperty()) {
                    case "email" -> orders.add(order.isAscending() ?
                            member.email.asc() : member.email.desc());
                    case "nickname" -> orders.add(order.isAscending() ?
                            member.nickname.asc() : member.nickname.desc());
                    case "createdAt" -> orders.add(order.isAscending() ?
                            member.createdAt.asc() : member.createdAt.desc());
                }
            });
        }

        return orders;
    }
}