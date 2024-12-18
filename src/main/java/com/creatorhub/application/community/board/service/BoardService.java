package com.creatorhub.application.community.board.service;

import com.creatorhub.application.community.board.dto.BoardCommand;
import com.creatorhub.application.community.board.dto.BoardResponse;
import com.creatorhub.application.community.board.exception.BoardNotFoundException;
import com.creatorhub.application.community.board.exception.DuplicateBoardNameException;
import com.creatorhub.application.community.board.port.in.GetBoardUseCase;
import com.creatorhub.application.community.board.port.in.ManageBoardUseCase;
import com.creatorhub.application.community.category.dto.CategoryResponse;
import com.creatorhub.application.community.common.validator.BusinessRuleValidator;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService implements ManageBoardUseCase, GetBoardUseCase {

    private final BoardRepository boardRepository;
    private final BusinessRuleValidator businessRuleValidator;

    @Override
    @Transactional
    public BoardResponse createBoard(BoardCommand.Create command) {
        if (boardRepository.existsByName(command.getName())) {
            throw new DuplicateBoardNameException(command.getName());
        }

        Board board = Board.builder()
                .name(command.getName())
                .description(command.getDescription())
                .isEnabled(command.isEnabled())
                .build();

        Board savedBoard = boardRepository.save(board);
        return toBoardResponse(savedBoard);
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(BoardCommand.Update command) {
        Board board = boardRepository.findById(command.getId())
                .orElseThrow(() -> new BoardNotFoundException(command.getId()));

        if (command.getName() != null &&
                !command.getName().equals(board.getName()) &&
                boardRepository.existsByName(command.getName())) {
            throw new DuplicateBoardNameException(command.getName());
        }

        if (command.getName() != null) {
            board.updateName(command.getName());
        }
        if (command.getDescription() != null) {
            board.updateDescription(command.getDescription());
        }
        if (command.getIsEnabled() != null) {
            board.updateStatus(command.getIsEnabled());
        }

        return toBoardResponse(board);
    }

    @Override
    @Transactional
    public void deleteBoard(UUID id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));

        board.validateBeforeDelete();
        boardRepository.deleteById(id);
    }

    @Override
    public BoardResponse getBoard(UUID id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));
        return toBoardResponse(board);
    }

    @Override
    public Page<BoardResponse> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(this::toBoardResponse);
    }

    @Override
    public Page<BoardResponse> getActiveBoards(Pageable pageable) {
        return boardRepository.findByIsEnabledTrue(pageable)
                .map(this::toBoardResponse);
    }

    private BoardResponse toBoardResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .isEnabled(board.isEnabled())
                .articleCount(board.getArticles().size())
                .categories(board.getCategories().stream()
                        .map(category -> CategoryResponse.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .isEnabled(category.isEnabled())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .createdBy(board.getCreatedBy())
                .build();
    }
}