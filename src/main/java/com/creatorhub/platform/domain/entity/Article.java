package com.creatorhub.platform.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Article updateTitle(String newTitle) {
        this.title = newTitle;
        return this;
    }

    public Article updateContent(String newContent) {
        this.content = newContent;
        return this;
    }

    public Article updateCategory(Category newCategory) {
        this.category = newCategory;
        return this;
    }

    public Article updateBoard(Board newBoard) {
        this.board = newBoard;
        return this;
    }
}