package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.community.board.dto.BoardCommand;
import com.creatorhub.application.community.board.dto.BoardResponse;
import com.creatorhub.application.community.board.exception.BoardNotFoundException;
import com.creatorhub.application.community.board.service.BoardService;
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

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @Test
    @DisplayName("게시판을 생성할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void createBoard() throws Exception {
        // given
        BoardCommand.Create command = BoardCommand.Create.builder()
                .name("테스트 게시판")
                .description("테스트 설명")
                .isEnabled(true)
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(UUID.randomUUID())
                .name(command.getName())
                .description(command.getDescription())
                .isEnabled(command.isEnabled())
                .articleCount(0)
                .build();

        given(boardService.createBoard(any(BoardCommand.Create.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(command.getName()))
                .andExpect(jsonPath("$.data.description").value(command.getDescription()));
    }

    @Test
    @DisplayName("ADMIN 권한이 없으면 게시판을 생성할 수 없다")
    @WithMockUser(roles = "USER")
    void createBoardWithoutAdminRole() throws Exception {
        // given
        BoardCommand.Create command = BoardCommand.Create.builder()
                .name("테스트 게시판")
                .description("테스트 설명")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("게시판을 ID로 조회할 수 있다")
    @WithMockUser
    void getBoard() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        BoardResponse response = BoardResponse.builder()
                .id(boardId)
                .name("테스트 게시판")
                .description("테스트 설명")
                .isEnabled(true)
                .articleCount(0)
                .build();

        given(boardService.getBoard(boardId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/boards/{id}", boardId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(boardId.toString()))
                .andExpect(jsonPath("$.data.name").value(response.getName()));
    }

    @Test
    @DisplayName("존재하지 않는 게시판을 조회하면 404 응답을 반환한다")
    @WithMockUser
    void getBoardNotFound() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        given(boardService.getBoard(boardId)).willThrow(new BoardNotFoundException(boardId));

        // when & then
        mockMvc.perform(get("/api/v1/boards/{id}", boardId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("전체 게시판을 페이징하여 조회할 수 있다")
    @WithMockUser
    void getAllBoards() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<BoardResponse> boards = List.of(
                BoardResponse.builder()
                        .id(UUID.randomUUID())
                        .name("게시판1")
                        .isEnabled(true)
                        .build(),
                BoardResponse.builder()
                        .id(UUID.randomUUID())
                        .name("게시판2")
                        .isEnabled(true)
                        .build()
        );
        Page<BoardResponse> boardPage = new PageImpl<>(boards, pageRequest, boards.size());

        given(boardService.getAllBoards(any(PageRequest.class))).willReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/v1/boards")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("활성화된 게시판만 조회할 수 있다")
    @WithMockUser
    void getActiveBoards() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<BoardResponse> boards = List.of(
                BoardResponse.builder()
                        .id(UUID.randomUUID())
                        .name("활성 게시판")
                        .isEnabled(true)
                        .build()
        );
        Page<BoardResponse> boardPage = new PageImpl<>(boards, pageRequest, boards.size());

        given(boardService.getActiveBoards(any(PageRequest.class))).willReturn(boardPage);

        // when & then
        mockMvc.perform(get("/api/v1/boards/active")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].enabled").value(true));
    }

    @Test
    @DisplayName("게시판을 수정할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void updateBoard() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        BoardCommand.Update command = BoardCommand.Update.builder()
                .id(boardId)
                .name("수정된 게시판")
                .description("수정된 설명")
                .isEnabled(false)
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(boardId)
                .name(command.getName())
                .description(command.getDescription())
                .isEnabled(command.getIsEnabled())
                .build();

        given(boardService.updateBoard(any(BoardCommand.Update.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/boards/{id}", boardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(command.getName()));
    }

    @Test
    @DisplayName("게시판을 삭제할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void deleteBoard() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/v1/boards/{id}", boardId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}