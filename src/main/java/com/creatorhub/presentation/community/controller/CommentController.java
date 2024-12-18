package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.community.comment.dto.CommentCommand;
import com.creatorhub.application.community.comment.dto.CommentResponse;
import com.creatorhub.application.community.comment.port.in.GetCommentUseCase;
import com.creatorhub.application.community.comment.port.in.ManageCommentUseCase;
import com.creatorhub.application.community.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "댓글", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ManageCommentUseCase manageCommentUseCase;
    private final GetCommentUseCase getCommentUseCase;
    private final CommentService commentService;

    @Operation(
            summary = "댓글 작성",
            description = "게시글에 새로운 댓글을 작성합니다. 로그인이 필요합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "댓글 작성 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CommentResponse> createComment(
            @Valid @RequestBody CommentCommand.Create command,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(manageCommentUseCase.createComment(command));
    }

    @Operation(
            summary = "댓글 상세 조회",
            description = "댓글 ID로 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    @GetMapping("/{id}")
    public ApiResponse<CommentResponse> getComment(
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable UUID id) {
        return ApiResponse.success(getCommentUseCase.getComment(id));
    }

    @Operation(
            summary = "게시글별 댓글 목록 조회",
            description = "특정 게시글의 댓글 목록을 페이징하여 조회합니다."
    )
    @Parameters({
            @Parameter(
                    name = "page",
                    description = "페이지 번호 (0부터 시작)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "0")
            ),
            @Parameter(
                    name = "size",
                    description = "페이지 크기",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "20")
            ),
            @Parameter(
                    name = "sort",
                    description = "정렬 기준 (예: createdAt,desc)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "string")
            )
    })
    @GetMapping("/articles/{articleId}")
    public ApiResponse<Page<CommentResponse>> getCommentsByArticle(
            @PathVariable UUID articleId,
            Pageable pageable) {
        Page<CommentResponse> comments = commentService.getCommentsByArticleId(articleId, pageable);
        return ApiResponse.success(comments);  // 페이지 데이터를 응답에 포함
    }

    @Operation(
            summary = "댓글 수정",
            description = "작성한 댓글을 수정합니다. 작성자 본인만 수정할 수 있습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CommentResponse> updateComment(
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody CommentCommand.Update command,
            @AuthenticationPrincipal UserDetails userDetails) {
        CommentCommand.Update updatedCommand = CommentCommand.Update.builder()
                .id(id)
                .content(command.getContent())
                .build();
        return ApiResponse.success(manageCommentUseCase.updateComment(command));
    }

    @Operation(
            summary = "댓글 삭제",
            description = "작성한 댓글을 삭제합니다. 작성자 본인 또는 관리자만 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @commentService.isCommentOwner(#id, #userDetails.username))")
    public ApiResponse<Void> deleteComment(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        manageCommentUseCase.deleteComment(id);
        return ApiResponse.success();
    }
}