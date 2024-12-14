package com.creatorhub.domain.community.repository;

import com.creatorhub.domain.community.entity.Category;
import com.creatorhub.domain.community.repository.search.CategorySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByIdWithArticles(UUID id);
    Page<Category> findAll(Pageable pageable);
    Page<Category> search(CategorySearchCondition condition, Pageable pageable);
    List<Category> findAllByBoardId(UUID boardId);
    void deleteById(UUID id);
    boolean existsByNameAndBoardId(String name, UUID boardId);
    Page<Category> findByBoardIdAndIsEnabledTrue(UUID boardId, Pageable pageable);
    long countByBoardId(UUID boardId);
}