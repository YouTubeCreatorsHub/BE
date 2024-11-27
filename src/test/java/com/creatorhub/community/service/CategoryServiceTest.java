package com.creatorhub.community.service;

import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.Fixture;
import com.creatorhub.domain.community.repository.CategoryRepository;
import com.creatorhub.domain.community.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private final Fixture fixture = new Fixture();

    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    CategoryService categoryService;

    @DisplayName("게시글의 카테고리 엔터티 Category를 생성할 수 있다.")
    @Test
    void createCategoryTest() {
        String categoryName = "공지";
        Category category = fixture.createCategory(categoryName);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory(categoryName);
        verify(categoryRepository).save(any(Category.class));
        assertThat(result.getName()).isEqualTo(categoryName);
    }

    @DisplayName("엔티티의 아이디로 카테고리 엔티티를 찾아 반환할 수 있다.")
    @Test
    void getCategoryTest() {
        String categoryName = "공지";
        Category category = fixture.createCategory(categoryName);

        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(category));

        Category result = categoryService.getCategory(1L);
        verify(categoryRepository).findById(any(Long.class));
        assertThat(result).isNotNull();
    }

    @DisplayName("잘못된 아이디로 엔티티를 찾으려하면 NoSuchElementException을 반환한다")
    @Test
    void cantGetBoardTest() {
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> categoryService.getCategory(999L));
        verify(categoryRepository).findById(any(Long.class));
    }

    @DisplayName("카테고리의 이름을 업데이트할 수 있다.")
    @Test
    void updateCategoryNameTest() {
        String oldName = "공지";
        String newName = "공지사항";
        Category category = fixture.createCategory(oldName);

        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        category.updateName(newName);

        Category result = categoryService.updateCategoryName(1L, newName);

        verify(categoryRepository).findById(any(Long.class));
        verify(categoryRepository).save(any(Category.class));
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(category.getName()).isEqualTo(newName);
    }

    @DisplayName("카테고리 이름을 삭제할 수 있다.")
    @Test
    void deleteCategoryNameTest() {
        Category category = fixture.createCategory("프로그래밍");

        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        Category result = categoryService.deleteCategoryName(1L);

        verify(categoryRepository).delete(any(Category.class));
        assertThat(result).isEqualTo(category);
    }
}