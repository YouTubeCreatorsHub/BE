package com.creatorhub.domain.community.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 10000;

    private String title;           // 제목
    private String content;         // 내용
    private Board board;            // 게시판
    private Category category;      // 카테고리 (optional)
    private int viewCount;          // 조회수
    private List<Comment> comments; // 댓글 목록
    private Member author;          // 작성자

    @Override
    protected void onCreate() {
        super.onCreate();
        this.viewCount = 0;
        this.comments = new ArrayList<>();
    }

    public void updateTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void updateCategory(Category category) {
        if (category != null && !category.getBoard().getId().equals(this.board.getId())) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "해당 게시판에 속하지 않은 카테고리입니다."
            );
        }
        this.category = category;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void validateBeforeDelete() {
        if (!comments.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "댓글이 존재하는 게시글은 삭제할 수 없습니다."
            );
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "제목은 필수입니다.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("제목은 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH)
            );
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "내용은 필수입니다.");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("내용은 %d자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH)
            );
        }
    }

    public void addComment(Comment comment) {
        if (comment == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "댓글이 null입니다.");
        }
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }
}
