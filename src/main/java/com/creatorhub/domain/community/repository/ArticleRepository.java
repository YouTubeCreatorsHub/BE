package com.creatorhub.domain.community.repository;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.repository.search.ArticleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository {
    Article save(Article article);
    Optional<Article> findById(UUID id);
    Page<Article> findAll(Pageable pageable);
    Page<Article> findAllByBoardId(UUID boardId, Pageable pageable);
    Page<Article> findByBoardIdAndCategoryId(UUID boardId, UUID categoryId, Pageable pageable);
    List<Article> findByMemberId(UUID memberId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    long countByBoardId(UUID boardId);
    Page<Article> search(ArticleSearchCondition condition, Pageable pageable);
}