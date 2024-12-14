package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.community.article.dto.ArticleCommand;
import com.creatorhub.application.community.article.dto.ArticleResponse;
import com.creatorhub.application.community.article.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @Test
    @DisplayName("게시글을 생성할 수 있다")
    @WithMockUser
    void createArticle() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ArticleCommand.Create command = ArticleCommand.Create.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .boardId(boardId)
                .categoryId(categoryId)
                .build();

        ArticleResponse response = ArticleResponse.builder()
                .id(UUID.randomUUID())
                .title(command.getTitle())
                .content(command.getContent())
                .boardId(boardId)
                .categoryId(categoryId)
                .viewCount(0)
                .build();

        given(articleService.createArticle(any(ArticleCommand.Create.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/articles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value(command.getTitle()))
                .andExpect(jsonPath("$.data.content").value(command.getContent()));
    }

    @Test
    @DisplayName("게시글을 ID로 조회할 수 있다")
    @WithMockUser
    void getArticle() throws Exception {
        // given
        UUID articleId = UUID.randomUUID();
        ArticleResponse response = ArticleResponse.builder()
                .id(articleId)
                .title("테스트 제목")
                .content("테스트 내용")
                .viewCount(0)
                .build();

        given(articleService.findById(articleId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/articles/{id}", articleId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(articleId.toString()))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()));
    }

    @Test
    @DisplayName("전체 게시글을 페이징하여 조회할 수 있다")
    @WithMockUser
    void getAllArticles() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ArticleResponse> articles = List.of(
                ArticleResponse.builder()
                        .id(UUID.randomUUID())
                        .title("제목1")
                        .content("내용1")
                        .viewCount(0)
                        .build(),
                ArticleResponse.builder()
                        .id(UUID.randomUUID())
                        .title("제목2")
                        .content("내용2")
                        .viewCount(0)
                        .build()
        );
        Page<ArticleResponse> articlePage = new PageImpl<>(articles, pageRequest, articles.size());

        given(articleService.findAll(any(PageRequest.class))).willReturn(articlePage);

        // when & then
        mockMvc.perform(get("/api/v1/articles")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("게시판별로 게시글을 조회할 수 있다")
    @WithMockUser
    void getArticlesByBoard() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ArticleResponse> articles = List.of(
                ArticleResponse.builder()
                        .id(UUID.randomUUID())
                        .title("제목1")
                        .content("내용1")
                        .boardId(boardId)
                        .viewCount(0)
                        .build()
        );
        Page<ArticleResponse> articlePage = new PageImpl<>(articles, pageRequest, articles.size());

        given(articleService.findAllByBoardId(boardId, pageRequest)).willReturn(articlePage);

        // when & then
        mockMvc.perform(get("/api/v1/articles/boards/{boardId}", boardId)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].boardId").value(boardId.toString()));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 게시글을 작성할 수 없다")
    void createArticleUnauthorized() throws Exception {
        // given
        ArticleCommand.Create command = ArticleCommand.Create.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .boardId(UUID.randomUUID())
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isForbidden());  // 403 Forbidden으로 변경
    }
}