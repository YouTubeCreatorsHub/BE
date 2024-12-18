package com.creatorhub.domain.community.repository;

import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.repository.search.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findById(UUID id);
    Optional<Board> findByIdWithCategories(UUID id);
    Page<Board> findAll(Pageable pageable);
    Page<Board> search(BoardSearchCondition condition, Pageable pageable);
    List<Board> findAllWithCategories();
    void deleteById(UUID id);
    boolean existsByName(String name);
    Page<Board> findByIsEnabledTrue(Pageable pageable);
    long countByIsEnabled(boolean isEnabled);
}
