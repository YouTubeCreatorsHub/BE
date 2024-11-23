package com.creatorhub.platform.community.service;

import com.creatorhub.platform.community.entity.Board;
import com.creatorhub.platform.community.repository.BoardRepository;
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

    public  Board updateBoardName(Long boardId, String newName){
        Board board = retrieveBoard(boardId);
        board.updateName(newName);
        return boardRepository.save(board);
    }

    public Board deleteBoard(Long boardId){
        Board board = retrieveBoard(boardId);
        boardRepository.delete(board);
        return board;
    }
}
