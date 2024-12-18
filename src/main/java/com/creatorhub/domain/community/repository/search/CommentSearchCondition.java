package com.creatorhub.domain.community.repository.search;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CommentSearchCondition {
    private String content;           // 댓글 내용
    private UUID articleId;           // 게시글 ID
    private UUID memberId;           // 작성자 ID
    private LocalDateTime startDate;  // 작성 시작일
    private LocalDateTime endDate;    // 작성 종료일
    private Boolean isDeleted;        // 삭제 여부
}
