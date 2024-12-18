package com.creatorhub.application.community.category.port.in;

import com.creatorhub.application.community.category.dto.CategoryCommand;
import com.creatorhub.application.community.category.dto.CategoryResponse;

import java.util.UUID;

public interface ManageCategoryUseCase {
    CategoryResponse createCategory(CategoryCommand.Create command);
    CategoryResponse updateCategory(CategoryCommand.Update command);
    void deleteCategory(UUID id);
}
