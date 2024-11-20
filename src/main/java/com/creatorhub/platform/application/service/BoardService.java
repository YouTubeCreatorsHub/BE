package com.creatorhub.platform.application.service;

import com.creatorhub.platform.domain.entity.Board;
import com.creatorhub.platform.domain.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board createBoard(String name) {
        return boardRepository.save(
                Board.builder()
                        .name(name)
                        .build()
        );
    }

    public Board retrieveBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);
    }

    public  Board updateBoardName(Board board, String newName){
        Board renamed = board.updateName(newName);
        return boardRepository.save(renamed);
    }

    public Board deleteBoard(Board board){
        boardRepository.delete(board);
        return board;
    }
}
