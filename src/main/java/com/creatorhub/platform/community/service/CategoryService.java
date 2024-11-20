package com.creatorhub.platform.community.service;

import com.creatorhub.platform.community.entity.Category;
import com.creatorhub.platform.community.repository.CategoryRepository;
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

    public Category updateCategoryName(Category category, String newNmae) {
        return categoryRepository.save(category.updateName(newNmae));
    }

    public Category deleteCategoryName(Category category) {
        categoryRepository.delete(category);
        return category;
    }
}
