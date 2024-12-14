package com.creatorhub.infrastructure.persistence.community.repository;

import com.creatorhub.domain.community.entity.Member;
import com.creatorhub.domain.community.repository.search.MemberSearchCondition;
import com.creatorhub.infrastructure.config.TestQuerydslConfig;
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
@Import({MemberQueryRepositoryImpl.class, TestQuerydslConfig.class})
class MemberQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberQueryRepository memberQueryRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @MockBean
    private S3Client s3Client;

    private MemberJpaEntity member1;
    private MemberJpaEntity member2;

    @BeforeEach
    void setUp() {
        // S3Client 모의 객체 설정
        when(s3Client.putBucketPolicy(any(PutBucketPolicyRequest.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());

        when(s3Client.putBucketCors(any(PutBucketCorsRequest.class)))
                .thenReturn(PutBucketCorsResponse.builder().build());
        // 테스트 데이터 생성
        member1 = memberJpaRepository.save(MemberJpaEntity.builder()
                .email("test1@test.com")
                .password("password1")
                .nickname("tester1")
                .role(Member.Role.USER)
                .isEnabled(true)
                .build());

        member2 = memberJpaRepository.save(MemberJpaEntity.builder()
                .email("admin@test.com")
                .password("password2")
                .nickname("admin")
                .role(Member.Role.ADMIN)
                .isEnabled(false)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("이메일로 회원을 검색할 수 있다")
    void searchByEmail() {
        // given
        MemberSearchCondition condition = MemberSearchCondition.byEmail("test1@test.com");
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MemberJpaEntity> result = memberQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("test1@test.com");
    }

    @Test
    @DisplayName("닉네임으로 회원을 검색할 수 있다")
    void searchByNickname() {
        // given
        MemberSearchCondition condition = MemberSearchCondition.byNickname("admin");
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MemberJpaEntity> result = memberQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNickname()).isEqualTo("admin");
    }

    @Test
    @DisplayName("역할로 회원을 검색할 수 있다")
    void searchByRole() {
        // given
        MemberSearchCondition condition = MemberSearchCondition.byRole(Member.Role.ADMIN);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MemberJpaEntity> result = memberQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRole()).isEqualTo(Member.Role.ADMIN);
    }

    @Test
    @DisplayName("활성화 상태로 회원을 검색할 수 있다")
    void searchByEnabledStatus() {
        // given
        MemberSearchCondition condition = MemberSearchCondition.builder()
                .isEnabled(true)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MemberJpaEntity> result = memberQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isEnabled()).isTrue();
    }

    @Test
    @DisplayName("가입일 기간으로 회원을 검색할 수 있다")
    void searchByDateRange() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        MemberSearchCondition condition = MemberSearchCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MemberJpaEntity> result = memberQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("정렬 조건에 따라 회원을 정렬할 수 있다")
    void searchWithSort() {
        // given
        MemberSearchCondition condition = MemberSearchCondition.builder().build();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "email"));

        // when
        Page<MemberJpaEntity> result = memberQueryRepository.search(condition, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getEmail())
                .isLessThan(result.getContent().get(1).getEmail());
    }
}