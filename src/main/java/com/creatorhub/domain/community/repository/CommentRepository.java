package com.creatorhub.domain.community.repository;

import com.creatorhub.domain.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(UUID id);
    Page<Comment> findPageByArticleId(UUID articleId, Pageable pageable);  // 페이징용
    List<Comment> findAllByArticleId(UUID articleId); // 전체 목록용
    void deleteById(UUID id);
    long countByArticleId(UUID articleId);
}