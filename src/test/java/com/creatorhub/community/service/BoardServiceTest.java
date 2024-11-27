package com.creatorhub.community.service;

import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.Fixture;
import com.creatorhub.domain.community.repository.BoardRepository;
import com.creatorhub.domain.community.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    private final Fixture fixture = new Fixture();
    @Mock
    BoardRepository boardRepository;
    @InjectMocks
    BoardService boardService;

    @DisplayName("게시판 엔티티 Board를 생성할 수 있다.")
    @Test
    void createBoardTest() {
        String name = "프로그래밍 게시판";
        Board board = fixture.createBoard(name);

        when(boardRepository.save(any(Board.class))).thenReturn(board);
        Board result = boardService.createBoard(name);

        assertThat(result.getName()).isEqualTo(name);
        verify(boardRepository).save(any(Board.class));
    }

    @DisplayName("엔티티의 아이디로 게시판 엔티티를 찾아 반환할 수 있다.")
    @Test
    void canRetrieveBoardTest() {
        String name = "프로그래밍 게시판";
        Board board = fixture.createBoard(name);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(board));
        Board result = boardService.retrieveBoard(1L);

        assertThat(result.getName()).isEqualTo(name);
        verify(boardRepository).findById(any(Long.class));
    }

    @DisplayName("잘못된 아이디로 엔티티를 찾으려하면 NoSuchElementException을 반환한다")
    @Test
    void cantRetrieveBoardTest() {
        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> boardService.retrieveBoard(999L));
        verify(boardRepository).findById(any(Long.class));
    }

    @DisplayName("게시판의 이름을 변경할 수 있다.")
    @Test
    void updateBoardNameTest() {
        String name = "프로그래밍 게시판";
        String newName = "고양이 게시판";
        Board board = fixture.createBoard(name);
        assertThat(board.getName()).isEqualTo(name);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(board.updateName(newName));

        Board result = boardService.updateBoardName(1L, newName);
        assertThat(result.getName()).isEqualTo(newName);

        verify(boardRepository).findById(any(Long.class));
        verify(boardRepository).save(any(Board.class));
    }

    @DisplayName("게시판의 아이디를 이용해 삭제할 수 있다.")
    @Test
    void deleteBoard() {
        String name = "프로그래밍 게시판";
        Board board = fixture.createBoard(name);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(board));
        doNothing().when(boardRepository).delete(board);

        boardService.deleteBoard(1L);

        verify(boardRepository).findById(any(Long.class));
        verify(boardRepository).delete(any(Board.class));
    }
}