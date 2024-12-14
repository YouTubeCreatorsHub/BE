package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Comment;
import com.creatorhub.domain.community.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequest {
    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotNull(message = "게시글은 필수입니다")
    private UUID articleId;

    @NotNull(message = "작성자는 필수입니다")
    private UUID memberId;

    public Comment toEntity() {
        return Comment.builder()
                .content(content)
                .article(Article.builder().id(articleId).build())
                .member(Member.builder().id(memberId).build())
                .build();
    }
}