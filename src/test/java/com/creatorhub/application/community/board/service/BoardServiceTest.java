package com.creatorhub.application.community.board.service;

import com.creatorhub.application.community.board.dto.BoardCommand;
import com.creatorhub.application.community.board.dto.BoardResponse;
import com.creatorhub.application.community.board.exception.BoardNotFoundException;
import com.creatorhub.application.community.board.exception.DuplicateBoardNameException;
import com.creatorhub.domain.community.entity.Board;
import com.creatorhub.domain.community.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시판을 생성할 수 있다")
    void createBoard() {
        // given
        BoardCommand.Create command = BoardCommand.Create.builder()
                .name("테스트 게시판")
                .description("테스트 설명")
                .isEnabled(true)
                .build();

        Board board = Board.builder()
                .name(command.getName())
                .articles(new ArrayList<>())
                .categories(new ArrayList<>())
                .description(command.getDescription())
                .isEnabled(command.isEnabled())
                .build();

        given(boardRepository.existsByName(command.getName())).willReturn(false);
        given(boardRepository.save(any(Board.class))).willReturn(board);

        // when
        BoardResponse response = boardService.createBoard(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(command.getName());
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("이미 존재하는 게시판 이름으로 생성하면 예외가 발생한다")
    void createBoardWithDuplicateName() {
        // given
        BoardCommand.Create command = BoardCommand.Create.builder()
                .name("중복 게시판")
                .build();

        given(boardRepository.existsByName(command.getName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> boardService.createBoard(command))
                .isInstanceOf(DuplicateBoardNameException.class);
    }

    @Test
    @DisplayName("게시판을 ID로 조회할 수 있다")
    void getBoard() {
        // given
        UUID boardId = UUID.randomUUID();
        Board board = Board.builder()
                .id(boardId)
                .articles(new ArrayList<>())
                .categories(new ArrayList<>())
                .name("테스트 게시판")
                .build();

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // when
        BoardResponse response = boardService.getBoard(boardId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(boardId);
        assertThat(response.getName()).isEqualTo("테스트 게시판");
    }

    @Test
    @DisplayName("존재하지 않는 게시판을 조회하면 예외가 발생한다")
    void getBoardNotFound() {
        // given
        UUID boardId = UUID.randomUUID();
        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoard(boardId))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @Test
    @DisplayName("전체 게시판을 페이징하여 조회할 수 있다")
    void getAllBoards() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Board> boards = List.of(
                Board.builder()
                        .name("게시판1")
                        .articles(new ArrayList<>())  // 빈 ArrayList로 초기화
                        .categories(new ArrayList<>())
                        .build(),
                Board.builder()
                        .name("게시판2")
                        .articles(new ArrayList<>())  // 빈 ArrayList로 초기화
                        .categories(new ArrayList<>())
                        .build()
        );
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());

        given(boardRepository.findAll(pageable)).willReturn(boardPage);

        // when
        Page<BoardResponse> responses = boardService.getAllBoards(pageable);

        // then
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("활성화된 게시판만 조회할 수 있다")
    void getActiveBoards() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Board> boards = List.of(
                Board.builder()
                        .name("활성 게시판")
                        .isEnabled(true)
                        .articles(new ArrayList<>())  // articles 초기화
                        .categories(new ArrayList<>())  // categories 초기화
                        .build()
        );
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());

        given(boardRepository.findByIsEnabledTrue(pageable)).willReturn(boardPage);

        // when
        Page<BoardResponse> responses = boardService.getActiveBoards(pageable);

        // then
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).isEnabled()).isTrue();
    }

    @Test
    @DisplayName("게시판을 수정할 수 있다")
    void updateBoard() {
        // given
        UUID boardId = UUID.randomUUID();
        BoardCommand.Update command = BoardCommand.Update.builder()
                .id(boardId)
                .name("수정된 게시판")
                .description("수정된 설명")
                .isEnabled(false)
                .build();

        Board board = Board.builder()
                .id(boardId)
                .name("원래 게시판")
                .articles(new ArrayList<>())
                .categories(new ArrayList<>())
                .description("원래 설명")
                .isEnabled(true)
                .build();

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(boardRepository.existsByName(command.getName())).willReturn(false);

        // when
        BoardResponse response = boardService.updateBoard(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(command.getName());
        assertThat(response.getDescription()).isEqualTo(command.getDescription());
        assertThat(response.isEnabled()).isEqualTo(command.getIsEnabled());
    }
}