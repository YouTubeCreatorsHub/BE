package com.creatorhub.application.community.comment.service;

import com.creatorhub.application.community.article.exception.ArticleNotFoundException;
import com.creatorhub.application.community.comment.dto.CommentCommand;
import com.creatorhub.application.community.comment.dto.CommentResponse;
import com.creatorhub.application.community.comment.exception.CommentNotFoundException;
import com.creatorhub.application.community.comment.exception.UnauthorizedCommentModificationException;
import com.creatorhub.application.community.comment.port.in.GetCommentUseCase;
import com.creatorhub.application.community.comment.port.in.ManageCommentUseCase;
import com.creatorhub.application.community.common.validator.ContentValidator;
import com.creatorhub.application.community.member.exception.MemberNotFoundException;
import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Comment;
import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.ArticleRepository;
import com.creatorhub.domain.community.repository.CommentRepository;
import com.creatorhub.domain.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService implements ManageCommentUseCase, GetCommentUseCase {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ContentValidator contentValidator;

    @Override
    @Transactional
    public CommentResponse createComment(CommentCommand.Create command) {
        contentValidator.validateCommentContent(command.getContent());

        Article article = articleRepository.findById(command.getArticleId())
                .orElseThrow(() -> new ArticleNotFoundException(command.getArticleId()));

        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(command.getMemberId()));

        Comment comment = Comment.builder()
                .content(command.getContent())
                .article(article)
                .member(member)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(CommentCommand.Update command) {
        contentValidator.validateCommentContent(command.getContent());

        Comment comment = commentRepository.findById(command.getId())
                .orElseThrow(() -> new CommentNotFoundException(command.getId()));

        validateCommentModificationAuthority(comment);

        comment.updateContent(command.getContent());
        return CommentResponse.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        validateCommentModificationAuthority(comment);

        commentRepository.deleteById(id);
    }

    @Override
    public CommentResponse getComment(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        return CommentResponse.from(comment);
    }

    @Override
    public Page<CommentResponse> getCommentsByArticleId(UUID articleId, Pageable pageable) {
        if (!articleRepository.existsById(articleId)) {
            throw new ArticleNotFoundException(articleId);
        }

        return commentRepository.findPageByArticleId(articleId, pageable)
                .map(CommentResponse::from);
    }

    private void validateCommentModificationAuthority(Comment comment) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getMember().getEmail().equals(currentUsername)) {
            throw new UnauthorizedCommentModificationException();
        }
    }
}