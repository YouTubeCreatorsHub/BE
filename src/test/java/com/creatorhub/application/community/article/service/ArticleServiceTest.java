package com.creatorhub.application.community.article.service;

import com.creatorhub.application.community.article.dto.ArticleCommand;
import com.creatorhub.application.community.article.dto.ArticleResponse;
import com.creatorhub.application.community.article.exception.ArticleNotFoundException;
import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.ArticleRepository;
import com.creatorhub.domain.community.repository.BoardRepository;
import com.creatorhub.domain.community.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("게시글을 생성할 수 있다")
    void createArticle() {
        // given
        UUID boardId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ArticleCommand.Create command = ArticleCommand.Create.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .boardId(boardId)
                .categoryId(categoryId)
                .build();

        Board board = Board.builder()
                .id(boardId)
                .name("테스트 게시판")
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("테스트 카테고리")
                .board(board)
                .build();

        Article article = Article.builder()
                .title(command.getTitle())
                .content(command.getContent())
                .board(board)
                .category(category)
                .build();

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(articleRepository.save(any(Article.class))).willReturn(article);

        // when
        ArticleResponse response = articleService.createArticle(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(command.getTitle());
        assertThat(response.getContent()).isEqualTo(command.getContent());
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    @DisplayName("게시글을 ID로 조회할 수 있다")
    void getArticle() {
        // given
        UUID articleId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        Board board = Board.builder()
                .id(boardId)
                .name("테스트 게시판")
                .build();
        Article article = Article.builder()
                .id(articleId)
                .title("테스트 제목")
                .content("테스트 내용")
                .board(board)
                .build();

        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleResponse response = articleService.findById(articleId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(articleId);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getBoardId()).isEqualTo(boardId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 조회하면 예외가 발생한다")
    void getArticleNotFound() {
        // given
        UUID articleId = UUID.randomUUID();
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleService.findById(articleId))
                .isInstanceOf(ArticleNotFoundException.class);
    }

    @Test
    @DisplayName("전체 게시글을 페이징하여 조회할 수 있다")
    void getAllArticles() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Board board = Board.builder()
                .id(UUID.randomUUID())
                .name("테스트 게시판")
                .build();

        List<Article> articles = List.of(
                Article.builder()
                        .title("제목1")
                        .content("내용1")
                        .board(board)  // Board 설정 추가
                        .build(),
                Article.builder()
                        .title("제목2")
                        .content("내용2")
                        .board(board)  // Board 설정 추가
                        .build()
        );
        Page<Article> articlePage = new PageImpl<>(articles, pageable, articles.size());

        given(articleRepository.findAll(pageable)).willReturn(articlePage);

        // when
        Page<ArticleResponse> responses = articleService.findAll(pageable);

        // then
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시판 ID로 게시글을 조회할 수 있다")
    void getArticlesByBoardId() {
        // given
        UUID boardId = UUID.randomUUID();
        Board board = Board.builder()
                .id(boardId)
                .name("테스트 게시판")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        List<Article> articles = List.of(
                Article.builder()
                        .title("제목1")
                        .content("내용1")
                        .board(board)  // Board 설정 추가
                        .build(),
                Article.builder()
                        .title("제목2")
                        .content("내용2")
                        .board(board)  // Board 설정 추가
                        .build()
        );
        Page<Article> articlePage = new PageImpl<>(articles, pageable, articles.size());

        given(articleRepository.findAllByBoardId(boardId, pageable)).willReturn(articlePage);

        // when
        Page<ArticleResponse> responses = articleService.findAllByBoardId(boardId, pageable);

        // then
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getTotalElements()).isEqualTo(2);
    }
}