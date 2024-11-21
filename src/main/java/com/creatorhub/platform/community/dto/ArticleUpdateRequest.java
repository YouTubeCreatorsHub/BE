package com.creatorhub.platform.community.dto;

import com.creatorhub.platform.community.entity.Board;
import com.creatorhub.platform.community.entity.Category;
import lombok.Data;

@Data
public class ArticleUpdateRequest {
    private String title;
    private String content;
    private Board board;
    private Category category;
}
