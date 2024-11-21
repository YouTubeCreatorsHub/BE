package com.creatorhub.platform.community.service;

import com.creatorhub.platform.community.entity.Article;
import com.creatorhub.platform.community.entity.Comment;
import com.creatorhub.platform.community.entity.Member;
import com.creatorhub.platform.community.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment createComment(String content, Article article, Member member) {
        return commentRepository.save(
                Comment.builder()
                        .content(content)
                        .article(article)
                        .member(member)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
    }

    public Page<Comment> getComments(Long articleId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findAllByArticleId(articleId, pageable);
    }

    public Comment updateComment(Long commentId, String newContent) {
        Comment comment = getComment(commentId);
        return commentRepository.save(comment.updateContent(newContent));
    }

    public Comment deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        commentRepository.delete(comment);
        return comment;
    }
}
