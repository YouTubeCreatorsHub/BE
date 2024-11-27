package com.creatorhub.community.service;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.Fixture;
import com.creatorhub.domain.community.repository.ArticleRepository;
import com.creatorhub.domain.community.service.ArticleService;
import org.assertj.core.api.Assertions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    private final Fixture fixture = new Fixture();

    @Mock
    ArticleRepository articleRepository;
    @InjectMocks
    ArticleService articleService;

    @DisplayName("게시글 엔티티 Article을 생성할 수 있다.")
    @Test
    void createArticleTest() {
        String title = "제목";
        String content = "내용";
        Board board = mock(Board.class);
        Category category = mock(Category.class);
        Article article = fixture.createArticle(title, content, board, category);

        when(articleRepository.save(any(Article.class))).thenReturn(article);

        Article result = articleService.createArticle(title, content, board, category);
        verify(articleRepository).save(any(Article.class));
        Assertions.assertThat(result.getTitle()).isEqualTo(title);
        Assertions.assertThat(result.getContent()).isEqualTo(content);
    }

    @DisplayName("엔티티 아이디로 게시글 엔티티를 찾아 반환할 수 있다.")
    @Test
    void getArticleTest() {
        String title = "제목";
        String content = "내용";
        Board board = mock(Board.class);
        Category category = mock(Category.class);
        Article article = fixture.createArticle(title, content, board, category);

        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(article));

        articleService.getArticle(1L);
        verify(articleRepository).findById(any(Long.class));
    }

    @DisplayName("게시판 ID로 게시글 목록을 페이징하여 조회할 수 있다")
    @Test
    void getArticlesTest() {
        Long boardId = 1L;
        int page = 0;
        int size = 10;
        List<Article> articles = new ArrayList<>();
        articles.add(fixture.createArticle("제목1", "내용1", mock(Board.class), mock(Category.class)));
        articles.add(fixture.createArticle("제목2", "내용2", mock(Board.class), mock(Category.class)));

        when(articleRepository.findAllByBoard_Id(eq(boardId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(articles, PageRequest.of(page, size), articles.size()));

        Page<Article> result = articleService.getArticles(boardId, page, size);
        verify(articleRepository).findAllByBoard_Id(eq(boardId), any(Pageable.class));
        assertEquals(2, result.getTotalElements());
    }

    @DisplayName("존재하지 않는 게시판 ID로 게시글 목록을 조회할 경우 빈 페이지를 반환한다")
    @Test
    void getArticlesWithInvalidBoardIdTest() {
        Long invalidBoardId = 999L;
        int page = 0;
        int size = 10;

        when(articleRepository.findAllByBoard_Id(eq(invalidBoardId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0));

        Page<Article> result = articleService.getArticles(invalidBoardId, page, size);
        verify(articleRepository).findAllByBoard_Id(eq(invalidBoardId), any(Pageable.class));
        assertEquals(0, result.getTotalElements());
    }

    @DisplayName("게시글 제목을 업데이트할 수 있다.")
    @Test
    void updateArticleTitleTest() {
        Long articleId = 1L;
        String newTitle = "새 제목";
        Article article = fixture.createArticle("기존 제목", "내용", mock(Board.class), mock(Category.class));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.updateArticleTitle(articleId, newTitle);
        Assertions.assertThat(article.getTitle()).isEqualTo(newTitle);
        verify(articleRepository).save(article);
    }

    @DisplayName("게시글 내용을 업데이트할 수 있다.")
    @Test
    void updateArticleContentTest() {
        Long articleId = 1L;
        String newContent = "새 내용";
        Article article = fixture.createArticle("제목", "기존 내용", mock(Board.class), mock(Category.class));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.updateArticleContent(articleId, newContent);
        Assertions.assertThat(article.getContent()).isEqualTo(newContent);
        verify(articleRepository).save(article);
    }

    @DisplayName("게시글의 게시판을 변경할 수 있다.")
    @Test
    void updateArticleBoardTest() {
        Long articleId = 1L;
        Board newBoard = mock(Board.class);
        Article article = fixture.createArticle("제목", "내용", mock(Board.class), mock(Category.class));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.updateArticleBoard(articleId, newBoard);
        Assertions.assertThat(article.getBoard()).isEqualTo(newBoard);
        verify(articleRepository).save(article);
    }

    @DisplayName("게시글의 카테고리를 변경할 수 있다.")
    @Test
    void updateArticleCategoryTest() {
        Long articleId = 1L;
        Category newCategory = mock(Category.class);
        Article article = fixture.createArticle("제목", "내용", mock(Board.class), newCategory);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.updateArticleCategory(articleId, newCategory);
        Assertions.assertThat(article.getCategory()).isEqualTo(newCategory);
        verify(articleRepository).save(article);
    }


    @DisplayName("게시글을 삭제할 수 있다.")
    @Test
    void deleteArticleTest() {
        Long articleId = 1L;
        Article article = fixture.createArticle("제목", "내용", mock(Board.class), mock(Category.class));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        doNothing().when(articleRepository).delete(any(Article.class));

        Article result = articleService.deleteArticle(articleId);

        verify(articleRepository).findById(articleId);
        verify(articleRepository).delete(article);
        assertEquals(article, result);
    }
}