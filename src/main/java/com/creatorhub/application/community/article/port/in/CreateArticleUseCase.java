package com.creatorhub.application.community.article.port.in;

import com.creatorhub.application.community.article.dto.ArticleCommand;
import com.creatorhub.application.community.article.dto.ArticleResponse;

public interface CreateArticleUseCase {
    ArticleResponse createArticle(ArticleCommand.Create command);
}