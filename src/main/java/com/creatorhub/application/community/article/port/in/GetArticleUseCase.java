package com.creatorhub.application.community.article.port.in;

import com.creatorhub.application.community.article.dto.ArticleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetArticleUseCase {
    ArticleResponse findById(UUID id);
    Page<ArticleResponse> findAll(Pageable pageable);
    Page<ArticleResponse> findAllByBoardId(UUID boardId, Pageable pageable);
}
