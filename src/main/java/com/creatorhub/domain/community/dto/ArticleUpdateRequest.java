package com.creatorhub.domain.community.dto;

import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.entity.Category;
import lombok.Data;

@Data
public class ArticleUpdateRequest {
    private String title;
    private String content;
    private Board board;
    private Category category;
}
