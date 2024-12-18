package com.creatorhub.application.community.category.service;

import com.creatorhub.application.community.category.dto.CategoryCommand;
import com.creatorhub.application.community.category.dto.CategoryResponse;
import com.creatorhub.application.community.category.exception.CategoryNotFoundException;
import com.creatorhub.application.community.category.exception.DuplicateCategoryNameException;
import com.creatorhub.application.community.common.validator.BusinessRuleValidator;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.BoardRepository;
import com.creatorhub.domain.community.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BusinessRuleValidator businessRuleValidator;

    @Test
    @DisplayName("카테고리를 생성할 수 있다")
    void createCategory() {
        // given
        UUID boardId = UUID.randomUUID();
        CategoryCommand.Create command = CategoryCommand.Create.builder()
                .name("테스트 카테고리")
                .boardId(boardId)
                .isEnabled(true)
                .build();

        Board board = Board.builder()
                .id(boardId)
                .name("테스트 게시판")
                .isEnabled(true)
                .build();

        Category category = Category.builder()
                .name(command.getName())
                .board(board)
                .articles(new ArrayList<>())
                .isEnabled(command.isEnabled())
                .build();

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(categoryRepository.existsByNameAndBoardId(command.getName(), boardId)).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // when
        CategoryResponse response = categoryService.createCategory(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(command.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("같은 게시판 내에 이미 존재하는 카테고리 이름으로 생성하면 예외가 발생한다")
    void createCategoryWithDuplicateName() {
        // given
        UUID boardId = UUID.randomUUID();
        CategoryCommand.Create command = CategoryCommand.Create.builder()
                .name("중복 카테고리")
                .boardId(boardId)
                .build();

        Board board = Board.builder()
                .id(boardId)
                .name("테스트 게시판")
                .articles(new ArrayList<>())
                .isEnabled(true)
                .build();

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(categoryRepository.existsByNameAndBoardId(command.getName(), boardId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(command))
                .isInstanceOf(DuplicateCategoryNameException.class);
    }

    @Test
    @DisplayName("카테고리를 ID로 조회할 수 있다")
    void getCategory() {
        // given
        UUID categoryId = UUID.randomUUID();
        Board board = Board.builder()
                .name("테스트 게시판")
                .isEnabled(true)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("테스트 카테고리")
                .articles(new ArrayList<>())
                .board(board)
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when
        CategoryResponse response = categoryService.getCategory(categoryId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(categoryId);
        assertThat(response.getName()).isEqualTo("테스트 카테고리");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 조회하면 예외가 발생한다")
    void getCategoryNotFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("게시판 ID로 카테고리 목록을 조회할 수 있다")
    void getCategoriesByBoard() {
        // given
        UUID boardId = UUID.randomUUID();
        Board board = Board.builder()
                .id(boardId)
                .name("테스트 게시판")
                .articles(new ArrayList<>())
                .isEnabled(true)
                .build();

        List<Category> categories = List.of(
                Category.builder()
                        .name("카테고리1")
                        .articles(new ArrayList<>())
                        .board(board)
                        .build(),
                Category.builder()
                        .name("카테고리2")
                        .articles(new ArrayList<>())
                        .board(board)
                        .build()
        );

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(categoryRepository.findAllByBoardId(boardId)).willReturn(categories);

        // when
        List<CategoryResponse> responses = categoryService.getCategoriesByBoardId(boardId);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(CategoryResponse::getName)
                .containsExactly("카테고리1", "카테고리2");
    }

    @Test
    @DisplayName("카테고리를 수정할 수 있다")
    void updateCategory() {
        // given
        UUID categoryId = UUID.randomUUID();
        Board board = Board.builder()
                .name("테스트 게시판")
                .isEnabled(true)
                .build();

        CategoryCommand.Update command = CategoryCommand.Update.builder()
                .id(categoryId)
                .name("수정된 카테고리")
                .isEnabled(false)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("원래 카테고리")
                .articles(new ArrayList<>())
                .board(board)
                .isEnabled(true)
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryRepository.existsByNameAndBoardId(command.getName(), board.getId())).willReturn(false);

        // when
        CategoryResponse response = categoryService.updateCategory(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(command.getName());
        assertThat(response.isEnabled()).isEqualTo(command.getIsEnabled());
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다")
    void deleteCategory() {
        // given
        UUID categoryId = UUID.randomUUID();
        Board board = Board.builder()
                .name("테스트 게시판")
                .isEnabled(true)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("테스트 카테고리")
                .articles(new ArrayList<>())
                .board(board)
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository).deleteById(categoryId);
    }
}