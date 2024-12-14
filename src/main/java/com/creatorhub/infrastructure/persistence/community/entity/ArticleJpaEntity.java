package com.creatorhub.infrastructure.persistence.community.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
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
        name = "articles",
        indexes = {
                @Index(name = "idx_articles_board_id", columnList = "board_id"),
                @Index(name = "idx_articles_category_id", columnList = "category_id"),
                @Index(name = "idx_articles_member_id", columnList = "member_id"),
                @Index(name = "idx_articles_created_at", columnList = "created_at")
        }
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardJpaEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryJpaEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberJpaEntity member;

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentJpaEntity> comments = new ArrayList<>();


    public void setMember(MemberJpaEntity member) {
        this.member = member;
    }

    public void setBoard(BoardJpaEntity board) {
        this.board = board;
    }

    public void addComment(CommentJpaEntity comment) {
        comments.add(comment);
        comment.setArticle(this);
    }

    public void removeComment(CommentJpaEntity comment) {
        comments.remove(comment);
        comment.setArticle(null);
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateCategory(CategoryJpaEntity category) {
        this.category = category;
    }
}