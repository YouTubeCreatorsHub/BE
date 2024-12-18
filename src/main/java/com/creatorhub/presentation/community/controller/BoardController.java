package com.creatorhub.presentation.community.controller;

import com.creatorhub.application.common.response.ApiResponse;
import com.creatorhub.application.community.board.dto.BoardCommand;
import com.creatorhub.application.community.board.dto.BoardResponse;
import com.creatorhub.application.community.board.port.in.GetBoardUseCase;
import com.creatorhub.application.community.board.port.in.ManageBoardUseCase;
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

@Tag(name = "게시판", description = "게시판 관련 API")
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {
    private final ManageBoardUseCase manageBoardUseCase;
    private final GetBoardUseCase getBoardUseCase;

    @Operation(summary = "게시판 생성", description = "새로운 게시판을 생성합니다. 관리자 권한이 필요합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BoardResponse> createBoard(
            @Valid @RequestBody BoardCommand.Create command) {
        return ApiResponse.success(manageBoardUseCase.createBoard(command));
    }

    @Operation(summary = "게시판 정보 조회", description = "게시판 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<BoardResponse> getBoard(@PathVariable UUID id) {
        return ApiResponse.success(getBoardUseCase.getBoard(id));
    }

    @Operation(summary = "게시판 목록 조회", description = "전체 게시판 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ApiResponse<Page<BoardResponse>> getAllBoards(Pageable pageable) {
        return ApiResponse.success(getBoardUseCase.getAllBoards(pageable));
    }

    @Operation(summary = "활성화된 게시판 목록 조회", description = "활성화된 게시판 목록을 조회합니다.")
    @GetMapping("/active")
    public ApiResponse<Page<BoardResponse>> getActiveBoards(Pageable pageable) {
        return ApiResponse.success(getBoardUseCase.getActiveBoards(pageable));
    }

    @Operation(summary = "게시판 삭제", description = "게시판을 삭제합니다. 관리자 권한이 필요합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBoard(@PathVariable UUID id) {
        manageBoardUseCase.deleteBoard(id);
    }

    @Operation(summary = "게시판 수정", description = "게시판 정보를 수정합니다. 관리자 권한이 필요합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BoardResponse> updateBoard(@PathVariable UUID id, @Valid @RequestBody BoardCommand.Update command) {
        BoardCommand.Update updateCommand = BoardCommand.Update.builder()
                .id(id)
                .name(command.getName())
                .description(command.getDescription())
                .isEnabled(command.getIsEnabled())
                .build();
        return ApiResponse.success(manageBoardUseCase.updateBoard(updateCommand));
    }
    // ... 추가 API 엔드포인트
}