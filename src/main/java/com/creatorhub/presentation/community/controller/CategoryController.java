package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.community.category.dto.CategoryCommand;
import com.creatorhub.application.community.category.dto.CategoryResponse;
import com.creatorhub.application.community.category.port.in.GetCategoryUseCase;
import com.creatorhub.application.community.category.port.in.ManageCategoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "카테고리", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ManageCategoryUseCase manageCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다. 관리자 권한이 필요합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCommand.Create command) {
        return ApiResponse.success(manageCategoryUseCase.createCategory(command));
    }

    @Operation(summary = "카테고리 정보 조회", description = "카테고리 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable UUID id) {
        return ApiResponse.success(getCategoryUseCase.getCategory(id));
    }

    @Operation(summary = "게시판별 카테고리 목록 조회", description = "특정 게시판의 카테고리 목록을 조회합니다.")
    @GetMapping("/boards/{boardId}")
    public ApiResponse<List<CategoryResponse>> getCategoriesByBoard(
            @PathVariable UUID boardId) {
        return ApiResponse.success(getCategoryUseCase.getCategoriesByBoardId(boardId));
    }

    // ... 추가 API 엔드포인트
}