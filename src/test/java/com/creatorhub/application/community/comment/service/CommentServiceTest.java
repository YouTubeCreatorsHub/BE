package com.creatorhub.application.community.comment.service;

import com.creatorhub.application.community.article.exception.ArticleNotFoundException;
import com.creatorhub.application.community.comment.dto.CommentCommand;
import com.creatorhub.application.community.comment.dto.CommentResponse;
import com.creatorhub.application.community.comment.exception.UnauthorizedCommentModificationException;
import com.creatorhub.application.community.common.validator.ContentValidator;
import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Comment;
import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.ArticleRepository;
import com.creatorhub.domain.community.repository.CommentRepository;
import com.creatorhub.domain.community.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ContentValidator contentValidator;

    private Member member;
    private Article article;
    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.builder()
                .name("테스트 게시판")
                .isEnabled(true)
                .build();

        member = Member.builder()
                .email("test@test.com")
                .nickname("테스터")
                .build();

        article = Article.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .board(board)
                .build();
    }

    @Test
    @DisplayName("댓글을 생성할 수 있다")
    void createComment() {
        // given
        UUID articleId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        CommentCommand.Create command = CommentCommand.Create.builder()
                .content("테스트 댓글")
                .articleId(articleId)
                .memberId(memberId)
                .build();

        Comment comment = Comment.builder()
                .content(command.getContent())
                .article(article)
                .member(member)
                .build();

        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        // when
        CommentResponse response = commentService.createComment(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(command.getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글을 작성하면 예외가 발생한다")
    void createCommentWithNonExistentArticle() {
        // given
        CommentCommand.Create command = CommentCommand.Create.builder()
                .content("테스트 댓글")
                .articleId(UUID.randomUUID())
                .memberId(UUID.randomUUID())
                .build();

        given(articleRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(command))
                .isInstanceOf(ArticleNotFoundException.class);
    }

    @Test
    @DisplayName("댓글을 ID로 조회할 수 있다")
    void getComment() {
        // given
        UUID commentId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId)
                .content("테스트 댓글")
                .article(article)
                .member(member)
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        CommentResponse response = commentService.getComment(commentId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(commentId);
        assertThat(response.getContent()).isEqualTo("테스트 댓글");
    }

    @Test
    @DisplayName("게시글별로 댓글을 페이징하여 조회할 수 있다")
    void getCommentsByArticle() {
        // given
        UUID articleId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Comment> comments = List.of(
                Comment.builder().content("댓글1").article(article).member(member).build(),
                Comment.builder().content("댓글2").article(article).member(member).build()
        );
        Page<Comment> commentPage = new PageImpl<>(comments, pageRequest, comments.size());

        given(articleRepository.existsById(articleId)).willReturn(true);
        given(commentRepository.findPageByArticleId(articleId, pageRequest)).willReturn(commentPage);

        // when
        Page<CommentResponse> responses = commentService.getCommentsByArticleId(articleId, pageRequest);

        // then
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글을 수정할 수 있다")
    void updateComment() {
        // given
        UUID commentId = UUID.randomUUID();
        CommentCommand.Update command = CommentCommand.Update.builder()
                .id(commentId)
                .content("수정된 댓글")
                .build();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("원래 댓글")
                .article(article)
                .member(member)
                .build();

        // Security Context Mock 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("test@test.com");
        SecurityContextHolder.setContext(securityContext);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        CommentResponse response = commentService.updateComment(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(command.getContent());
    }

    @Test
    @DisplayName("다른 사용자의 댓글을 수정하면 예외가 발생한다")
    void updateCommentByOtherUser() {
        // given
        UUID commentId = UUID.randomUUID();
        CommentCommand.Update command = CommentCommand.Update.builder()
                .id(commentId)
                .content("수정된 댓글")
                .build();

        Member otherMember = Member.builder()
                .email("other@test.com")
                .nickname("다른 사용자")
                .build();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("원래 댓글")
                .article(article)
                .member(otherMember)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("test@test.com");
        SecurityContextHolder.setContext(securityContext);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(command))
                .isInstanceOf(UnauthorizedCommentModificationException.class);
    }
}