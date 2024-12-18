package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.search.CommentSearchCondition;
import com.creatorhub.infrastructure.config.TestQuerydslConfig;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.CommentJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.MemberJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({CommentQueryRepositoryImpl.class, TestQuerydslConfig.class})
class CommentQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CommentQueryRepository commentQueryRepository;

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;
    @MockBean
    private S3Client s3Client;

    private MemberJpaEntity member;
    private BoardJpaEntity board;
    private ArticleJpaEntity article;
    private CommentJpaEntity comment1;
    private CommentJpaEntity comment2;

    @BeforeEach
    void setUp() {
        // S3Client 모의 객체 설정
        when(s3Client.putBucketPolicy(any(PutBucketPolicyRequest.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());

        when(s3Client.putBucketCors(any(PutBucketCorsRequest.class)))
                .thenReturn(PutBucketCorsResponse.builder().build());
        // 테스트 데이터 생성
        member = memberJpaRepository.save(MemberJpaEntity.builder()
                .email("test@test.com")
                .nickname("tester")
                .password("password")
                .role(Member.Role.USER)
                .build());

        board = boardJpaRepository.save(BoardJpaEntity.builder()
                .name("test board")
                .isEnabled(true)
                .build());

        article = articleJpaRepository.save(ArticleJpaEntity.builder()
                .title("test article")
                .content("test content")
                .member(member)
                .board(board)
                .viewCount(0)
                .build());

        comment1 = commentJpaRepository.save(CommentJpaEntity.builder()
                .content("test comment 1")
                .article(article)
                .member(member)
                .build());

        comment2 = commentJpaRepository.save(CommentJpaEntity.builder()
                .content("different comment")
                .article(article)
                .member(member)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("내용으로 댓글을 검색할 수 있다")
    void searchByContent() {
        // given
        CommentSearchCondition condition = CommentSearchCondition.builder()
                .content("test")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CommentJpaEntity> result = commentQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).contains("test");
    }

    @Test
    @DisplayName("게시글 ID로 댓글을 검색할 수 있다")
    void searchByArticleId() {
        // given
        CommentSearchCondition condition = CommentSearchCondition.builder()
                .articleId(article.getId())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CommentJpaEntity> result = commentQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting("article")
                .extracting("id")
                .containsOnly(article.getId());
    }

    @Test
    @DisplayName("작성자 ID로 댓글을 검색할 수 있다")
    void searchByAuthorId() {
        // given
        CommentSearchCondition condition = CommentSearchCondition.builder()
                .memberId(member.getId())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CommentJpaEntity> result = commentQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting("member")
                .extracting("id")
                .containsOnly(member.getId());
    }

    @Test
    @DisplayName("기간으로 댓글을 검색할 수 있다")
    void searchByDateRange() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        CommentSearchCondition condition = CommentSearchCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CommentJpaEntity> result = commentQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("정렬 조건에 따라 댓글을 정렬할 수 있다")
    void searchWithSort() {
        // given
        CommentSearchCondition condition = CommentSearchCondition.builder().build();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<CommentJpaEntity> result = commentQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCreatedAt())
                .isAfterOrEqualTo(result.getContent().get(1).getCreatedAt());
    }
}