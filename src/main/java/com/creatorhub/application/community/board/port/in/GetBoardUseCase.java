package com.creatorhub.application.community.board.port.in;

import com.creatorhub.application.community.board.dto.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetBoardUseCase {
    BoardResponse getBoard(UUID id);
    Page<BoardResponse> getAllBoards(Pageable pageable);
    Page<BoardResponse> getActiveBoards(Pageable pageable);
}