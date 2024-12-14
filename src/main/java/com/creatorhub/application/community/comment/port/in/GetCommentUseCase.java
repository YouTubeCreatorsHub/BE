package com.creatorhub.application.community.comment.port.in;

import com.creatorhub.application.community.comment.dto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetCommentUseCase {
    CommentResponse getComment(UUID id);
    Page<CommentResponse> getCommentsByArticleId(UUID articleId, Pageable pageable);
}