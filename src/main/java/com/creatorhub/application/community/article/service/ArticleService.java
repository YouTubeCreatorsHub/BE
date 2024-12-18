package com.creatorhub.application.community.article.service;

import com.creatorhub.application.community.article.dto.ArticleCommand;
import com.creatorhub.application.community.article.dto.ArticleResponse;
import com.creatorhub.application.community.article.exception.ArticleNotFoundException;
import com.creatorhub.application.community.article.port.in.CreateArticleUseCase;
import com.creatorhub.application.community.article.port.in.GetArticleUseCase;
import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.ArticleRepository;
import com.creatorhub.domain.community.repository.BoardRepository;
import com.creatorhub.domain.community.repository.CategoryRepository;
import com.creatorhub.infrastructure.persistence.community.mapper.ArticleJpaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService implements CreateArticleUseCase, GetArticleUseCase {
    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleJpaMapper articleJpaMapper;

    @Override
    @Transactional
    public ArticleResponse createArticle(ArticleCommand.Create command) {
        Board board = boardRepository.findById(command.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다."));

        Category category = null;
        if (command.getCategoryId() != null) {
            category = categoryRepository.findById(command.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
        }

        Article article = Article.builder()
                .title(command.getTitle())
                .content(command.getContent())
                .board(board)
                .category(category)
                .viewCount(0)
                .build();

        Article savedArticle = articleRepository.save(article);
        return ArticleResponse.from(savedArticle);
    }

    @Override
    public ArticleResponse findById(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        article.increaseViewCount();
        return ArticleResponse.from(article);
    }

    @Override
    public Page<ArticleResponse> findAll(Pageable pageable) {  // getAllArticles -> findAll
        return articleRepository.findAll(pageable)
                .map(ArticleResponse::from);
    }
    @Override
    public Page<ArticleResponse> findAllByBoardId(UUID boardId, Pageable pageable) {  // getArticlesByBoardId -> findAllByBoardId
        return articleRepository.findAllByBoardId(boardId, pageable)
                .map(ArticleResponse::from);
    }
}