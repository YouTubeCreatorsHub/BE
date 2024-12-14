package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.repository.search.BoardSearchCondition;
import com.creatorhub.infrastructure.config.TestQuerydslConfig;
import com.creatorhub.infrastructure.persistence.community.entity.BoardJpaEntity;
import com.creatorhub.infrastructure.persistence.community.entity.CategoryJpaEntity;
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
@Import({BoardQueryRepositoryImpl.class, TestQuerydslConfig.class})
class BoardQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BoardQueryRepository boardQueryRepository;

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    @MockBean
    private S3Client s3Client;
    private BoardJpaEntity board1;
    private BoardJpaEntity board2;
    private CategoryJpaEntity category;

    @BeforeEach
    void setUp() {
        // S3Client 모의 객체 설정
        when(s3Client.putBucketPolicy(any(PutBucketPolicyRequest.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());

        when(s3Client.putBucketCors(any(PutBucketCorsRequest.class)))
                .thenReturn(PutBucketCorsResponse.builder().build());
        // 테스트 데이터 생성
        board1 = boardJpaRepository.save(BoardJpaEntity.builder()
                .name("test board 1")
                .description("test description 1")
                .isEnabled(true)
                .build());

        board2 = boardJpaRepository.save(BoardJpaEntity.builder()
                .name("test board 2")
                .description("different description")
                .isEnabled(false)
                .build());

        category = categoryJpaRepository.save(CategoryJpaEntity.builder()
                .name("test category")
                .board(board1)
                .isEnabled(true)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("키워드로 게시판을 검색할 수 있다")
    void searchByKeyword() {
        // given
        BoardSearchCondition condition = BoardSearchCondition.builder()
                .searchKeyword("test")
                .isEnabled(null)  // isEnabled를 null로 설정하여 모든 게시판 검색
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<BoardJpaEntity> result = boardQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).contains("test");
    }

    @Test
    @DisplayName("활성화 상태로 게시판을 검색할 수 있다")
    void searchByEnabledStatus() {
        // given
        BoardSearchCondition condition = BoardSearchCondition.onlyEnabled();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<BoardJpaEntity> result = boardQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isEnabled()).isTrue();
    }

    @Test
    @DisplayName("카테고리 보유 여부로 게시판을 검색할 수 있다")
    void searchByHasCategories() {
        // given
        BoardSearchCondition condition = BoardSearchCondition.builder()
                .hasCategories(true)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<BoardJpaEntity> result = boardQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategories()).isNotEmpty();
    }

    @Test
    @DisplayName("게시글 수 범위로 게시판을 검색할 수 있다")
    void searchByArticleCount() {
        // given
        BoardSearchCondition condition = BoardSearchCondition.builder()
                .minArticleCount(0)
                .maxArticleCount(5)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<BoardJpaEntity> result = boardQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }
}