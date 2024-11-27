package com.creatorhub.domain.community.service;

import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(String name) {
        return categoryRepository.save(
                Category.builder()
                        .name(name)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(NoSuchElementException::new);
    }

    public Category updateCategoryName(Long categoryId, String newNmae) {
        Category category = getCategory(categoryId);
        return categoryRepository.save(category.updateName(newNmae));
    }

    public Category deleteCategoryName(Long categoryId) {
        Category category = getCategory(categoryId);
        categoryRepository.delete(category);
        return category;
    }
}
