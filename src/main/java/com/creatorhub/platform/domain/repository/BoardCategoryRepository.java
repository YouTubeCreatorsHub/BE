package com.creatorhub.platform.domain.repository;

import com.creatorhub.platform.domain.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
}
