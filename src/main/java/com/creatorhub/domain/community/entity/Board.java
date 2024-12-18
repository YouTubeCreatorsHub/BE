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
public class Board extends BaseEntity {
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private String name;            // 게시판 이름
    private String description;     // 게시판 설명
    private boolean isEnabled;      // 활성화 여부
    private List<Category> categories;  // 카테고리 목록
    private List<Article> articles;     // 게시글 목록

    @Override
    protected void onCreate() {
        super.onCreate();
        this.isEnabled = true;
        this.categories = new ArrayList<>();
        this.articles = new ArrayList<>();
    }

    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    public void updateDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public void updateStatus(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void validateBeforeDelete() {
        if (!categories.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "카테고리가 존재하는 게시판은 삭제할 수 없습니다."
            );
        }
        if (!articles.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "게시글이 존재하는 게시판은 삭제할 수 없습니다."
            );
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "게시판 이름은 필수입니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("게시판 이름은 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH)
            );
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("게시판 설명은 %d자를 초과할 수 없습니다.", MAX_DESCRIPTION_LENGTH)
            );
        }
    }

    public void addCategory(Category category) {
        if (category == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "카테고리가 null입니다.");
        }
        this.categories.add(category);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
    }

    public void addArticle(Article article) {
        if (article == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "게시글이 null입니다.");
        }
        this.articles.add(article);
    }

    public void removeArticle(Article article) {
        this.articles.remove(article);
    }
}