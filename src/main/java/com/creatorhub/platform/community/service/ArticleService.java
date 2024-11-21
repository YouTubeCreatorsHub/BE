package com.creatorhub.platform.community.service;

import com.creatorhub.platform.community.entity.Article;
import com.creatorhub.platform.community.entity.Board;
import com.creatorhub.platform.community.entity.Category;
import com.creatorhub.platform.community.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Article createArticle(String title, String content, Board board, Category categor) {
        return articleRepository.save(
                Article.builder()
                        .title(title)
                        .content(content)
                        .board(board)
                        .category(categor)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public Article getArticle(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(NoSuchElementException::new);
    }

    public Page<Article> getArticles(Long boardId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findAllByBoard_Id(boardId, pageable);
    }

    public Article updateArticleTitle(Long articleId, String title) {
        Article article = getArticle(articleId);
        return articleRepository.save(article.updateTitle(title));
    }

    public Article updateArticleContent(Long articleId, String content) {
        Article article = getArticle(articleId);
        return articleRepository.save(article.updateContent(content));
    }

    public Article updateArticleCategory(Long articleId, Category category) {
        Article article = getArticle(articleId);
        return articleRepository.save(article.updateCategory(category));
    }

    public Article updateArticleBoard(Long articleId, Board board) {
        Article article = getArticle(articleId);
        return articleRepository.save(article.updateBoard(board));
    }

    public Article deleteArticle(Long articleId) {
        Article article = getArticle(articleId);
        articleRepository.delete(article);
        return article;
    }
}
