package com.creatorhub.infrastructure.persistence.community.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_article_id", columnList = "article_id"),
                @Index(name = "idx_comments_member_id", columnList = "member_id"),
                @Index(name = "idx_comments_created_at", columnList = "created_at")
        }
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentJpaEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleJpaEntity article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberJpaEntity member;

    protected void setArticle(ArticleJpaEntity article) {
        this.article = article;
    }

    protected void setMember(MemberJpaEntity member) {
        this.member = member;
    }
}