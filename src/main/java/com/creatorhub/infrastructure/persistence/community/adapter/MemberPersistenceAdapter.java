package com.creatorhub.infrastructure.persistence.community.adapter;

import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.MemberRepository;
import com.creatorhub.domain.community.repository.search.MemberSearchCondition;
import com.creatorhub.infrastructure.persistence.community.entity.MemberJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.QMemberJpaEntity;
import com.creatorhub.infrastructure.persistence.community.mapper.MemberJpaMapper;
import com.creatorhub.infrastructure.persistence.community.repository.MemberJpaRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRepository {
    private final JPAQueryFactory queryFactory;
    private final MemberJpaMapper memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaMapper.toDomain(
                memberJpaRepository.save(
                        memberJpaMapper.toJpaEntity(member)
                )
        );
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QMemberJpaEntity.memberJpaEntity)
                        .where(QMemberJpaEntity.memberJpaEntity.id.eq(id))
                        .fetchOne()
        ).map(memberJpaMapper::toDomain);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QMemberJpaEntity.memberJpaEntity)
                        .where(QMemberJpaEntity.memberJpaEntity.email.eq(email))
                        .fetchOne()
        ).map(memberJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(QMemberJpaEntity.memberJpaEntity)
                .where(QMemberJpaEntity.memberJpaEntity.email.eq(email))
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public boolean existsByNickname(String nickname) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(QMemberJpaEntity.memberJpaEntity)
                .where(QMemberJpaEntity.memberJpaEntity.nickname.eq(nickname))
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public void deleteById(UUID id) {
        memberJpaRepository.deleteById(id);
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return search(MemberSearchCondition.builder().build(), pageable);
    }

    public Page<Member> search(MemberSearchCondition condition, Pageable pageable) {
        QMemberJpaEntity member = QMemberJpaEntity.memberJpaEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 이메일 검색
        if (condition.getEmail() != null) {
            builder.and(member.email.containsIgnoreCase(condition.getEmail()));
        }

        // 닉네임 검색
        if (condition.getNickname() != null) {
            builder.and(member.nickname.containsIgnoreCase(condition.getNickname()));
        }

        // 역할 검색
        if (condition.getRole() != null) {
            builder.and(member.role.eq(condition.getRole()));
        }

        // 활성화 여부 검색
        if (condition.getIsEnabled() != null) {
            builder.and(member.isEnabled.eq(condition.getIsEnabled()));
        }

        // 가입일 기간 검색
        if (condition.getStartDate() != null) {
            builder.and(member.createdAt.goe(condition.getStartDate()));
        }
        if (condition.getEndDate() != null) {
            builder.and(member.createdAt.loe(condition.getEndDate()));
        }

        // 삭제 여부
        if (condition.getIsDeleted() != null) {
            builder.and(member.deleted.eq(condition.getIsDeleted()));
        }

        JPAQuery<MemberJpaEntity> query = queryFactory
                .selectFrom(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 조건 적용
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("createdAt")) {
                    query.orderBy(order.isAscending() ? member.createdAt.asc() : member.createdAt.desc());
                } else if (order.getProperty().equals("email")) {
                    query.orderBy(order.isAscending() ? member.email.asc() : member.email.desc());
                } else if (order.getProperty().equals("nickname")) {
                    query.orderBy(order.isAscending() ? member.nickname.asc() : member.nickname.desc());
                }
            });
        } else {
            query.orderBy(member.createdAt.desc());  // 기본 정렬: 최신순
        }

        List<Member> members = query.fetch().stream()
                .map(memberJpaMapper::toDomain)
                .collect(Collectors.toList());

        long total = queryFactory
                .selectFrom(member)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(members, pageable, total);
    }
}