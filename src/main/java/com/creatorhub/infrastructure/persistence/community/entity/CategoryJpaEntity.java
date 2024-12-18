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
        name = "categories",
        indexes = {
                @Index(name = "idx_categories_board_id", columnList = "board_id"),
                @Index(name = "idx_categories_name_board_id", columnList = "name,board_id", unique = true),
                @Index(name = "idx_categories_created_at", columnList = "created_at")
        }
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardJpaEntity board;

    @Column(nullable = false)
    @Builder.Default
    private boolean isEnabled = true;

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ArticleJpaEntity> articles = new ArrayList<>();

    public void setBoard(BoardJpaEntity board) {
        this.board = board;
    }

    public void addArticle(ArticleJpaEntity article) {
        articles.add(article);
        article.updateCategory(this);
    }

    public void removeArticle(ArticleJpaEntity article) {
        articles.remove(article);
        article.updateCategory(null);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}