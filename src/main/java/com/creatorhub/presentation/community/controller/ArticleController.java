package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.community.article.dto.ArticleCommand;
import com.creatorhub.application.community.article.dto.ArticleResponse;
import com.creatorhub.application.community.article.port.in.CreateArticleUseCase;
import com.creatorhub.application.community.article.port.in.GetArticleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final CreateArticleUseCase createArticleUseCase;
    private final GetArticleUseCase getArticleUseCase;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ArticleResponse> createArticle(
            @Valid @RequestBody ArticleCommand.Create command) {
        return ApiResponse.success(createArticleUseCase.createArticle(command));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<ArticleResponse> getArticle(@PathVariable UUID id) {
        return ApiResponse.success(getArticleUseCase.findById(id));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ApiResponse<Page<ArticleResponse>> getAllArticles(Pageable pageable) {
        return ApiResponse.success(getArticleUseCase.findAll(pageable));
    }

    @Operation(summary = "게시판별 게시글 목록 조회", description = "특정 게시판의 게시글 목록을 조회합니다.")
    @GetMapping("/boards/{boardId}")
    public ApiResponse<Page<ArticleResponse>> getArticlesByBoard(
            @PathVariable UUID boardId,
            Pageable pageable) {
        return ApiResponse.success(getArticleUseCase.findAllByBoardId(boardId, pageable));
    }
    // ... 추가 API 엔드포인트
}
