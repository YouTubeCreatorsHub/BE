package com.creatorhub.application.community.common.validator;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ContentValidator {
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 10000;
    private static final int MIN_LENGTH = 1;
    private static final int MAX_CATEGORY_NAME_LENGTH = 30;
    private static final int MAX_BOARD_NAME_LENGTH = 50;
    private static final int MAX_BOARD_DESCRIPTION_LENGTH = 200;
    private static final int MAX_COMMENT_LENGTH = 500;

    public void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "제목은 필수입니다.");
        }
        if (title.length() < MIN_LENGTH || title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("제목은 %d-%d자 사이여야 합니다.", MIN_LENGTH, MAX_TITLE_LENGTH)
            );
        }
    }

    public void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "내용은 필수입니다.");
        }
        if (content.length() < MIN_LENGTH || content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("내용은 %d-%d자 사이여야 합니다.", MIN_LENGTH, MAX_CONTENT_LENGTH)
            );
        }
    }

    public void validateCategoryName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "카테고리 이름은 필수입니다.");
        }
        if (name.length() < MIN_LENGTH || name.length() > MAX_CATEGORY_NAME_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("카테고리 이름은 %d-%d자 사이여야 합니다.", MIN_LENGTH, MAX_CATEGORY_NAME_LENGTH)
            );
        }
    }

    public void validateBoardName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "게시판 이름은 필수입니다.");
        }
        if (name.length() < MIN_LENGTH || name.length() > MAX_BOARD_NAME_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("게시판 이름은 %d-%d자 사이여야 합니다.", MIN_LENGTH, MAX_BOARD_NAME_LENGTH)
            );
        }
    }

    public void validateBoardDescription(String description) {
        if (description != null && description.length() > MAX_BOARD_DESCRIPTION_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("게시판 설명은 %d자를 초과할 수 없습니다.", MAX_BOARD_DESCRIPTION_LENGTH)
            );
        }
    }

    public void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "댓글 내용은 필수입니다.");
        }
        if (content.length() < MIN_LENGTH || content.length() > MAX_COMMENT_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("댓글 내용은 %d-%d자 사이여야 합니다.", MIN_LENGTH, MAX_COMMENT_LENGTH)
            );
        }
    }
}