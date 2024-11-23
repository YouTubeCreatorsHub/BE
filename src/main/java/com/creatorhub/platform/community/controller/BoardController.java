package com.creatorhub.platform.community.controller;

import com.creatorhub.platform.community.dto.BoardRequest;
import com.creatorhub.platform.community.entity.Board;
import com.creatorhub.platform.community.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardRequest request) {
        Board board = boardService.createBoard(request.getName());
        return ResponseEntity.ok(board);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoard(@PathVariable Long boardId) {
        Board board = boardService.retrieveBoard(boardId);
        return ResponseEntity.ok(board);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequest request) {
        Board updatedBoard = boardService.updateBoardName(boardId, request.getName());
        return ResponseEntity.ok(updatedBoard);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Board> deleteBoard(@PathVariable Long boardId) {
        Board deletedBoard = boardService.deleteBoard(boardId);
        return ResponseEntity.ok(deletedBoard);
    }
}

