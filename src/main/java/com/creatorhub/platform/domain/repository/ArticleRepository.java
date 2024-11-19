package com.creatorhub.platform.domain.repository;

import com.creatorhub.platform.domain.entity.Article;
import com.creatorhub.platform.domain.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByBoard(Board board, Pageable pageable);}
