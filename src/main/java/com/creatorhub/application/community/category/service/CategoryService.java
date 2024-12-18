package com.creatorhub.application.community.category.service;

import com.creatorhub.application.community.board.exception.BoardNotFoundException;
import com.creatorhub.application.community.category.dto.CategoryCommand;
import com.creatorhub.application.community.category.dto.CategoryResponse;
import com.creatorhub.application.community.category.exception.CategoryNotFoundException;
import com.creatorhub.application.community.category.exception.DuplicateCategoryNameException;
import com.creatorhub.application.community.category.port.in.GetCategoryUseCase;
import com.creatorhub.application.community.category.port.in.ManageCategoryUseCase;
import com.creatorhub.application.community.common.validator.BusinessRuleValidator;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.BoardRepository;
import com.creatorhub.domain.community.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService implements ManageCategoryUseCase, GetCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;
    private final BusinessRuleValidator businessRuleValidator;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCommand.Create command) {
        Board board = boardRepository.findById(command.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException(command.getBoardId()));

        businessRuleValidator.validateEnabled(board.isEnabled(), "게시판");

        if (categoryRepository.existsByNameAndBoardId(command.getName(), command.getBoardId())) {
            throw new DuplicateCategoryNameException(command.getName(), command.getBoardId());
        }

        Category category = Category.builder()
                .name(command.getName())
                .board(board)
                .isEnabled(command.isEnabled())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return toCategoryResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategoryCommand.Update command) {
        Category category = categoryRepository.findById(command.getId())
                .orElseThrow(() -> new CategoryNotFoundException(command.getId()));

        businessRuleValidator.validateEnabled(category.getBoard().isEnabled(), "게시판");

        if (command.getName() != null &&
                !command.getName().equals(category.getName()) &&
                categoryRepository.existsByNameAndBoardId(command.getName(), category.getBoard().getId())) {
            throw new DuplicateCategoryNameException(command.getName(), category.getBoard().getId());
        }

        if (command.getName() != null) {
            category.updateName(command.getName());
        }
        if (command.getIsEnabled() != null) {
            category.updateStatus(command.getIsEnabled());
        }

        return toCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        businessRuleValidator.validateEnabled(category.getBoard().isEnabled(), "게시판");

        // 카테고리에 연결된 게시글들의 카테고리를 null로 설정
        category.getArticles().forEach(article -> article.updateCategory(null));

        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse getCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        businessRuleValidator.validateEnabled(category.getBoard().isEnabled(), "게시판");
        return toCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getCategoriesByBoardId(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        businessRuleValidator.validateEnabled(board.isEnabled(), "게시판");

        return categoryRepository.findAllByBoardId(boardId).stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .boardId(category.getBoard().getId())
                .boardName(category.getBoard().getName())
                .isEnabled(category.isEnabled())
                .articleCount(category.getArticles().size())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .createdBy(category.getCreatedBy())
                .build();
    }
}