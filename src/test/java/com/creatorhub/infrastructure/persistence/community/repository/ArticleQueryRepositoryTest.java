package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.search.ArticleSearchCondition;
import com.creatorhub.infrastructure.config.TestQuerydslConfig;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({ArticleQueryRepositoryImpl.class, TestQuerydslConfig.class})
class ArticleQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ArticleQueryRepository articleQueryRepository;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @MockBean
    private S3Client s3Client;

    private MemberJpaEntity member;
    private BoardJpaEntity board;
    private ArticleJpaEntity article1;
    private ArticleJpaEntity article2;

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

        article1 = articleJpaRepository.save(ArticleJpaEntity.builder()
                .title("test article 1")
                .content("test content 1")
                .member(member)
                .board(board)
                .viewCount(0)
                .build());

        article2 = articleJpaRepository.save(ArticleJpaEntity.builder()
                .title("test article 2")
                .content("test different content")
                .member(member)
                .board(board)
                .viewCount(10)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("키워드로 게시글을 검색할 수 있다")
    void searchByKeyword() {
        // given
        ArticleSearchCondition condition = ArticleSearchCondition.of("test");
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleJpaEntity> result = articleQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).contains("test");
    }

    @Test
    @DisplayName("게시판 ID로 게시글을 검색할 수 있다")
    void searchByBoardId() {
        // given
        ArticleSearchCondition condition = ArticleSearchCondition.builder()
                .boardId(board.getId())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleJpaEntity> result = articleQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting("board")
                .extracting("id")
                .containsOnly(board.getId());
    }

    @Test
    @DisplayName("작성자 ID로 게시글을 검색할 수 있다")
    void searchByAuthorId() {
        // given
        ArticleSearchCondition condition = ArticleSearchCondition.builder()
                .memberId(member.getId())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleJpaEntity> result = articleQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting("member")
                .extracting("id")
                .containsOnly(member.getId());
    }

    @Test
    @DisplayName("기간으로 게시글을 검색할 수 있다")
    void searchByDateRange() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        ArticleSearchCondition condition = ArticleSearchCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleJpaEntity> result = articleQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("정렬 조건에 따라 게시글을 정렬할 수 있다")
    void searchWithSort() {
        // given
        ArticleSearchCondition condition = ArticleSearchCondition.builder().build();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "viewCount"));

        // when
        Page<ArticleJpaEntity> result = articleQueryRepository.search(condition, pageRequest);

        // then
        List<ArticleJpaEntity> content = result.getContent();
        assertThat(content).hasSize(2);
        assertThat(content.get(0).getViewCount()).isEqualTo(10);
        assertThat(content.get(1).getViewCount()).isEqualTo(0);
    }
}