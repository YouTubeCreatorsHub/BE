package com.creatorhub.platform;

import com.creatorhub.platform.community.entity.Article;
import com.creatorhub.platform.community.entity.Board;
import com.creatorhub.platform.community.entity.Category;

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
}
