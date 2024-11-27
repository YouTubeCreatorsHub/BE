package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Article;
import com.creatorhub.domain.community.entity.Member;
import lombok.Data;

@Data
public class CommentCreateRequest {
    private final String comment;
    private final Article article;
    private final Member member;
}
