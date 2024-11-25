package com.creatorhub.platform.community.repository;

import com.creatorhub.platform.community.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByBoard_Id(Long board, Pageable pageable);}
