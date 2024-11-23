package com.creatorhub.platform.community.dto;

import com.creatorhub.platform.community.entity.Article;
import lombok.Data;
import com.creatorhub.platform.community.entity.Member;

@Data
public class CommentCreateRequest {
    private final String comment;
    private final Article article;
    private final Member member;
}
