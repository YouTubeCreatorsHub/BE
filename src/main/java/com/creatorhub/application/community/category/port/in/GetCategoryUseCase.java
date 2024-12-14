package com.creatorhub.application.community.category.port.in;

import com.creatorhub.application.community.category.dto.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface GetCategoryUseCase {
    CategoryResponse getCategory(UUID id);
    List<CategoryResponse> getCategoriesByBoardId(UUID boardId);
}
