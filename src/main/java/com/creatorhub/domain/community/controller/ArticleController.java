package com.creatorhub.domain.community.controller;

import com.creatorhub.domain.community.dto.ArticleRequest;
import com.creatorhub.domain.community.dto.ArticleUpdateRequest;
import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody ArticleRequest request) {
        Article article = articleService.createArticle(
                request.getTitle(),
                request.getContent(),
                request.getBoard(),
                request.getCategory()
        );
        return ResponseEntity.ok(article);
    }

    @GetMapping
    public ResponseEntity<Page<Article>> getAllArticles(@RequestParam Long boardId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(articleService.getArticles(boardId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdateRequest request) {
        Article article = articleService.getArticle(id);

        if (request.getTitle() != null) {
            article = articleService.updateArticleTitle(id, request.getTitle());
        }
        if (request.getContent() != null) {
            article = articleService.updateArticleContent(id, request.getContent());
        }
        if (request.getCategory() != null) {
            article = articleService.updateArticleCategory(id, request.getCategory());
        }
        if (request.getBoard() != null) {
            article = articleService.updateArticleBoard(id, request.getBoard());
        }

        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }
}