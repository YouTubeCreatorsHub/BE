package com.creatorhub;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.entity.Comment;

import java.time.LocalDateTime;

public class Fixture {
    public Board createBoard(String name) {
        return Board.builder()
                .id(1L)
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Article createArticle(String title, String content, Board board, Category category) {
        return Article.builder()
                .id(1L)
                .title(title)
                .content(content)
                .board(board)
                .category(category)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
    public Category createCategory(String name){
        return Category.builder()
                .id(1L)
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public Comment createComment(String content, Article article) {
        return Comment.builder()
                .id(1L)
                .content(content)
                .createdAt(LocalDateTime.now())
                .article(article)
                .build();
    }
}
