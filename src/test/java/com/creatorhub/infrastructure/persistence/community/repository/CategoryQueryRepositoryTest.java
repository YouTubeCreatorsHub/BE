package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.search.CategorySearchCondition;
import com.creatorhub.infrastructure.config.TestQuerydslConfig;
import com.creatorhub.infrastructure.persistence.community.entity.ArticleJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
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
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({CategoryQueryRepositoryImpl.class, TestQuerydslConfig.class})
class CategoryQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryQueryRepository categoryQueryRepository;

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;
    @MockBean
    private S3Client s3Client;

    private BoardJpaEntity board;
    private CategoryJpaEntity category1;
    private CategoryJpaEntity category2;
    private MemberJpaEntity member;
    private ArticleJpaEntity article;

    @BeforeEach
    void setUp() {
        // S3Client 모의 객체 설정
        when(s3Client.putBucketPolicy(any(PutBucketPolicyRequest.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());

        when(s3Client.putBucketCors(any(PutBucketCorsRequest.class)))
                .thenReturn(PutBucketCorsResponse.builder().build());
        // 테스트 데이터 생성
        board = boardJpaRepository.save(BoardJpaEntity.builder()
                .name("test board")
                .isEnabled(true)
                .build());

        category1 = categoryJpaRepository.save(CategoryJpaEntity.builder()
                .name("test category 1")
                .board(board)
                .isEnabled(true)
                .build());

        category2 = categoryJpaRepository.save(CategoryJpaEntity.builder()
                .name("test category 2")
                .board(board)
                .isEnabled(false)
                .build());

        member = memberJpaRepository.save(MemberJpaEntity.builder()
                .email("test@test.com")
                .nickname("tester")
                .password("password")
                .role(Member.Role.USER)
                .build());

        article = articleJpaRepository.save(ArticleJpaEntity.builder()
                .title("test article")
                .content("test content")
                .board(board)
                .category(category1)
                .member(member)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("키워드로 카테고리를 검색할 수 있다")
    void searchByKeyword() {
        // given
        CategorySearchCondition condition = CategorySearchCondition.builder()
                .searchKeyword("test")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CategoryJpaEntity> result = categoryQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).contains("test");
    }

    @Test
    @DisplayName("게시판 ID로 카테고리를 검색할 수 있다")
    void searchByBoardId() {
        // given
        CategorySearchCondition condition = CategorySearchCondition.builder()
                .boardId(board.getId())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CategoryJpaEntity> result = categoryQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting("board")
                .extracting("id")
                .containsOnly(board.getId());
    }

    @Test
    @DisplayName("활성화 상태로 카테고리를 검색할 수 있다")
    void searchByEnabledStatus() {
        // given
        CategorySearchCondition condition = CategorySearchCondition.builder()
                .isEnabled(true)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CategoryJpaEntity> result = categoryQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isEnabled()).isTrue();
    }

    @Test
    @DisplayName("게시글 보유 여부로 카테고리를 검색할 수 있다")
    void searchByHasArticles() {
        // given
        CategorySearchCondition condition = CategorySearchCondition.builder()
                .hasArticles(true)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CategoryJpaEntity> result = categoryQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getArticles()).isNotEmpty();
    }
}