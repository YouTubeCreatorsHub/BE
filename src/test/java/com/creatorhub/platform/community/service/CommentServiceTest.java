package com.creatorhub.platform.community.service;

import com.creatorhub.platform.Fixture;
import com.creatorhub.platform.community.entity.Article;
import com.creatorhub.platform.community.entity.Comment;
import com.creatorhub.platform.community.entity.Member;
import com.creatorhub.platform.community.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private final Fixture fixture = new Fixture();

    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    CommentService commentService;

    @DisplayName("댓글 엔티티 Comment를 생성할 수 있다.")
    @Test
    void createCommentTest() {
        String content = "댓글 내용";
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        Comment comment = fixture.createComment(content, article);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.createComment(content, article, member);
        verify(commentRepository).save(any(Comment.class));
        assertThat(result.getContent()).isEqualTo(content);
    }

    @DisplayName("엔티티 아이디로 댓글 엔티티를 찾아 반환할 수 있다.")
    @Test
    void getCommentTest() {
        Long commentId = 1L;
        String content = "댓글 내용";
        Article article = mock(Article.class);
        Comment comment = fixture.createComment(content, article);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getComment(commentId);
        verify(commentRepository).findById(commentId);
        assertThat(result.getContent()).isEqualTo(content);
    }

    @DisplayName("존재하지 않는 댓글 아이디로 조회할 경우 NoSuchElementException을 발생시킨다.")
    @Test
    void cantGetCommentTest() {
        when(commentRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentService.getComment(999L));
        verify(commentRepository).findById(any(Long.class));
    }

    @DisplayName("댓글을 업데이트할 수 있다.")
    @Test
    void updateCommentTest() {
        Long commentId = 1L;
        String newContent = "업데이트된 댓글 내용";
        Article article = mock(Article.class);
        Comment comment = fixture.createComment("기존 댓글 내용", article);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment.updateContent(newContent));

        Comment result = commentService.updateComment(commentId, newContent);
        verify(commentRepository).save(comment);
        assertThat(result.getContent()).isEqualTo(newContent);
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void deleteCommentTest() {
        Long commentId = 1L;
        Article article = mock(Article.class);
        Comment comment = fixture.createComment("댓글 내용", article);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(any(Comment.class));

        Comment result = commentService.deleteComment(commentId);
        verify(commentRepository).delete(comment);
        assertThat(result).isEqualTo(comment);
    }

    @DisplayName("댓글 목록을 페이징하여 조회할 수 있다.")
    @Test
    void getCommentsTest() {
        Long articleId = 1L;
        Article mock = mock(Article.class);
        List<Comment> comments = new ArrayList<>();
        comments.add(fixture.createComment("댓글1", mock));
        comments.add(fixture.createComment("댓글2", mock));

        when(commentRepository.findAllByArticleId(any(Long.class), any())).thenReturn(new PageImpl<>(comments));

        Page<Comment> result = commentService.getComments(articleId, 0, 10);
        verify(commentRepository).findAllByArticleId(any(Long.class), any());
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
}