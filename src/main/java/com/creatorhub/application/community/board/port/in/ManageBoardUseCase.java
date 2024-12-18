package com.creatorhub.application.community.board.port.in;

import com.creatorhub.application.community.board.dto.BoardCommand;
import com.creatorhub.application.community.board.dto.BoardResponse;

import java.util.UUID;

public interface ManageBoardUseCase {
    BoardResponse createBoard(BoardCommand.Create command);
    BoardResponse updateBoard(BoardCommand.Update command);
    void deleteBoard(UUID id);
}