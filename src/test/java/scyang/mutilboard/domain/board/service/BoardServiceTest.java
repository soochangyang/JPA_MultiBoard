package scyang.mutilboard.domain.board.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import scyang.mutilboard.domain.board.dto.BoardRequest;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;
import scyang.mutilboard.domain.board.entity.Board;
import scyang.mutilboard.domain.board.repository.BoardRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@Slf4j
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createBoard_success() throws Exception {
        //given
        BoardRequest.Create request = new BoardRequest.Create("등업게시판", "등급관련 문의는 여기에서", true);

        Board savedBoard = new Board(request.getName(), request.getDescription(), request.getActive());
        setId(savedBoard, 1L);

        given(boardRepository.save(any(Board.class))).willReturn(savedBoard);

        Long boardId = boardService.createBoard(request);

        //then
        assertThat(boardId).isEqualTo(1L);
    }

    @Test
    void updateBoard_success() {
        //given
        Long boardId = 1L;
        BoardRequest.Update request = new BoardRequest.Update("수정 게시판", "수정관련 문의는 여기에서", true);

        Board existingBoard = Board.builder()
                .name("기존 게시판")
                .description("기존 설명")
                .active(true)
                .build();
        setId(existingBoard, boardId);

        given(boardRepository.findById(eq(boardId))).willReturn(Optional.of(existingBoard));

        boardService.updateBoard(boardId, request);

        //then
        assertThat(existingBoard.getName()).isEqualTo(request.getName());
        assertThat(existingBoard.getDescription()).isEqualTo(request.getDescription());
        assertThat(existingBoard.getActive()).isEqualTo(request.getActive());
    }



    @Test
    void getBoard_success() {
        //given
        Long boardId = 1L;
        Board board = Board.builder()
                .name("Free board")
                .description("feel free to write here")
                .active(true)
                .build();
        setId(board, 1L);

        //
        given(boardRepository.findById(eq(boardId))).willReturn(Optional.of(board));

        //when
        BoardResponse response = boardService.getBoard(boardId);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(board.getName());
        assertThat(response.getDescription()).isEqualTo(board.getDescription());
    }

    @Test
    void searchBoards_success() throws Exception {
        BoardSearchCondition condition = new BoardSearchCondition("free");
        Pageable pageable = PageRequest.of(0, 10);

        List<BoardResponse> boardList = new ArrayList<>();
        for(int i =0; i < 10; i ++) {
            BoardResponse boardResponse = new BoardResponse(Long.valueOf(i), "freeboard "+ i, "free to write "+i, true);
            boardList.add(boardResponse);
        }

        Page<BoardResponse> boardPage = new PageImpl<>(boardList, pageable, 10);

        //Page<BoardResponse>

        given(boardRepository.searchBoardPage(any(), any())).willReturn(boardPage);

        //when
        Page<BoardResponse> resultPage = boardService.searchBoards(condition, pageable);

        //then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(10);
        assertThat(resultPage.getContent()).extracting("name").containsExactly("freeboard 0",
                "freeboard 1",
                "freeboard 2",
                "freeboard 3",
                "freeboard 4",
                "freeboard 5",
                "freeboard 6",
                "freeboard 7",
                "freeboard 8",
                "freeboard 9");
    }

    @Test
    void deleteById_success() {
        //given
        Long boardId = 1L;

        Board board = Board.builder()
                .name("삭제할 게시판")
                .description("description")
                .active(true)
                .build();
        setId(board, boardId);

        //given(boardRepository.findById(eq(boardId))).willReturn(Optional.of(board));

        boardService.deleteById(boardId);

        //then
        then(boardRepository).should().deleteById(boardId);
    }

    private void setId(Object target, Long id){
        try{
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}