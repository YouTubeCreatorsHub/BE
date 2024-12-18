package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.community.category.dto.CategoryCommand;
import com.creatorhub.application.community.category.dto.CategoryResponse;
import com.creatorhub.application.community.category.exception.CategoryNotFoundException;
import com.creatorhub.application.community.category.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("카테고리를 생성할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void createCategory() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        CategoryCommand.Create command = CategoryCommand.Create.builder()
                .name("테스트 카테고리")
                .boardId(boardId)
                .isEnabled(true)
                .build();

        CategoryResponse response = CategoryResponse.builder()
                .id(UUID.randomUUID())
                .name(command.getName())
                .boardId(boardId)
                .isEnabled(command.isEnabled())
                .articleCount(0)
                .build();

        given(categoryService.createCategory(any(CategoryCommand.Create.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(command.getName()))
                .andExpect(jsonPath("$.data.boardId").value(boardId.toString()));
    }

    @Test
    @DisplayName("ADMIN 권한이 없으면 카테고리를 생성할 수 없다")
    @WithMockUser(roles = "USER")
    void createCategoryWithoutAdminRole() throws Exception {
        // given
        CategoryCommand.Create command = CategoryCommand.Create.builder()
                .name("테스트 카테고리")
                .boardId(UUID.randomUUID())
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("카테고리를 ID로 조회할 수 있다")
    @WithMockUser
    void getCategory() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        CategoryResponse response = CategoryResponse.builder()
                .id(categoryId)
                .name("테스트 카테고리")
                .boardId(boardId)
                .isEnabled(true)
                .build();

        given(categoryService.getCategory(categoryId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value(response.getName()));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 조회하면 404 응답을 반환한다")
    @WithMockUser
    void getCategoryNotFound() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();
        given(categoryService.getCategory(categoryId)).willThrow(new CategoryNotFoundException(categoryId));

        // when & then
        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("게시판별로 카테고리 목록을 조회할 수 있다")
    @WithMockUser
    void getCategoriesByBoard() throws Exception {
        // given
        UUID boardId = UUID.randomUUID();
        List<CategoryResponse> categories = List.of(
                CategoryResponse.builder()
                        .id(UUID.randomUUID())
                        .name("카테고리1")
                        .boardId(boardId)
                        .isEnabled(true)
                        .build(),
                CategoryResponse.builder()
                        .id(UUID.randomUUID())
                        .name("카테고리2")
                        .boardId(boardId)
                        .isEnabled(true)
                        .build()
        );

        given(categoryService.getCategoriesByBoardId(boardId)).willReturn(categories);

        // when & then
        mockMvc.perform(get("/api/v1/categories/boards/{boardId}", boardId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].boardId").value(boardId.toString()));
    }

    @Test
    @DisplayName("카테고리를 수정할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void updateCategory() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();
        CategoryCommand.Update command = CategoryCommand.Update.builder()
                .id(categoryId)
                .name("수정된 카테고리")
                .isEnabled(false)
                .build();

        CategoryResponse response = CategoryResponse.builder()
                .id(categoryId)
                .name(command.getName())
                .isEnabled(command.getIsEnabled())
                .build();

        given(categoryService.updateCategory(any(CategoryCommand.Update.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(command.getName()));
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다")
    @WithMockUser(roles = "ADMIN")
    void deleteCategory() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();
        doNothing().when(categoryService).deleteCategory(categoryId);

        // when & then
        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}