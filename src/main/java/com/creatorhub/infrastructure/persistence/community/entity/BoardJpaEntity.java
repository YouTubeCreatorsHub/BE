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
        name = "boards",
        indexes = {
                @Index(name = "idx_boards_name", columnList = "name", unique = true),
                @Index(name = "idx_boards_created_at", columnList = "created_at")
        }
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardJpaEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean isEnabled = true;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<CategoryJpaEntity> categories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<ArticleJpaEntity> articles = new ArrayList<>();

    public void addCategory(CategoryJpaEntity category) {
        categories.add(category);
        category.setBoard(this);
    }

    public void removeCategory(CategoryJpaEntity category) {
        categories.remove(category);
        category.setBoard(null);
    }

    public void addArticle(ArticleJpaEntity article) {
        articles.add(article);
        article.setBoard(this);
    }

    public void removeArticle(ArticleJpaEntity article) {
        articles.remove(article);
        article.setBoard(null);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}