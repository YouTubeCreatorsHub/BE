package com.creatorhub.domain.community.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    private static final int MAX_CONTENT_LENGTH = 500;

    private String content;     // 댓글 내용
    private Article article;    // 게시글
    private Member member;      // 작성자

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "댓글 내용은 필수입니다.");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("댓글 내용은 %d자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH)
            );
        }
    }

    public boolean isAuthor(Member member) {
        return this.member.getId().equals(member.getId());
    }
}