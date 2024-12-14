package com.creatorhub.infrastructure.persistence.community.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import com.creatorhub.domain.community.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "members",
        indexes = {
                @Index(name = "idx_members_email", columnList = "email", unique = true),
                @Index(name = "idx_members_nickname", columnList = "nickname"),
                @Index(name = "idx_members_created_at", columnList = "created_at")
        }
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Member.Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean isEnabled = true;
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleJpaEntity> articles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentJpaEntity> comments = new ArrayList<>();

    public void addArticle(ArticleJpaEntity article) {
        articles.add(article);
        article.setMember(this);
    }

    public void removeArticle(ArticleJpaEntity article) {
        articles.remove(article);
        article.setMember(null);
    }

    public void addComment(CommentJpaEntity comment) {
        comments.add(comment);
        comment.setMember(this);
    }

    public void removeComment(CommentJpaEntity comment) {
        comments.remove(comment);
        comment.setMember(null);
    }
}