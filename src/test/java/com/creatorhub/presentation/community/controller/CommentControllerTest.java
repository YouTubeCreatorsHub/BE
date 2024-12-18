package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.community.comment.dto.CommentCommand;
import com.creatorhub.application.community.comment.dto.CommentResponse;
import com.creatorhub.application.community.comment.exception.CommentNotFoundException;
import com.creatorhub.application.community.comment.exception.UnauthorizedCommentModificationException;
import com.creatorhub.application.community.comment.service.CommentService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("댓글을 작성할 수 있다")
    @WithMockUser
    void createComment() throws Exception {
        // given
        UUID articleId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        CommentCommand.Create command = CommentCommand.Create.builder()
                .content("테스트 댓글")
                .articleId(articleId)
                .memberId(memberId)
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(UUID.randomUUID())
                .content(command.getContent())
                .articleId(articleId)
                .memberId(memberId)
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.createComment(any(CommentCommand.Create.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value(command.getContent()));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 댓글을 작성할 수 없다")
    void createCommentUnauthorized() throws Exception {
        // given
        CommentCommand.Create command = CommentCommand.Create.builder()
                .content("테스트 댓글")
                .articleId(UUID.randomUUID())
                .memberId(UUID.randomUUID())
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글을 ID로 조회할 수 있다")
    @WithMockUser
    void getComment() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content("테스트 댓글")
                .articleId(UUID.randomUUID())
                .memberId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.getComment(commentId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/comments/{id}", commentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(commentId.toString()))
                .andExpect(jsonPath("$.data.content").value(response.getContent()));
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 조회하면 404 응답을 반환한다")
    @WithMockUser
    void getCommentNotFound() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        given(commentService.getComment(commentId)).willThrow(new CommentNotFoundException(commentId));

        // when & then
        mockMvc.perform(get("/api/v1/comments/{id}", commentId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("게시글별로 댓글 목록을 조회할 수 있다")
    @WithMockUser
    void getCommentsByArticle() throws Exception {
        // given
        UUID articleId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<CommentResponse> comments = List.of(
                CommentResponse.builder()
                        .id(UUID.randomUUID())
                        .content("댓글1")
                        .articleId(articleId)
                        .build(),
                CommentResponse.builder()
                        .id(UUID.randomUUID())
                        .content("댓글2")
                        .articleId(articleId)
                        .build()
        );
        Page<CommentResponse> commentPage = new PageImpl<>(comments, pageRequest, comments.size());

        given(commentService.getCommentsByArticleId(articleId, pageRequest)).willReturn(commentPage);

        // when & then
        mockMvc.perform(get("/api/v1/comments/articles/{articleId}", articleId)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("자신의 댓글을 수정할 수 있다")
    @WithMockUser
    void updateComment() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        CommentCommand.Update command = CommentCommand.Update.builder()
                .id(commentId)
                .content("수정된 댓글")
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content(command.getContent())
                .build();

        given(commentService.updateComment(any(CommentCommand.Update.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/comments/{id}", commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value(command.getContent()));
    }

    @Test
    @DisplayName("다른 사용자의 댓글을 수정하면 403 응답을 반환한다")
    @WithMockUser
    void updateCommentUnauthorized() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        CommentCommand.Update command = CommentCommand.Update.builder()
                .id(commentId)
                .content("수정된 댓글")
                .build();

        given(commentService.updateComment(any(CommentCommand.Update.class)))
                .willThrow(new UnauthorizedCommentModificationException());

        // when & then
        mockMvc.perform(put("/api/v1/comments/{id}", commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("댓글을 삭제할 수 있다")
    @WithMockUser(username = "testUser")
    void deleteComment() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        given(commentService.isCommentOwner(commentId, "testUser")).willReturn(true);
        doNothing().when(commentService).deleteComment(commentId);

        // when & then
        mockMvc.perform(delete("/api/v1/comments/{id}", commentId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}