package scyang.mutilboard.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scyang.mutilboard.domain.board.dto.BoardRequest;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;
import scyang.mutilboard.domain.board.entity.Board;
import scyang.mutilboard.domain.board.repository.BoardRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Long createBoard(BoardRequest.Create request){
        Board board = request.toEntity();
        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    @Transactional
    public void updateBoard(Long boardId, BoardRequest.Update request){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("not found board"));
        findBoard.updateInfo(request.getName(),
                request.getDescription(),
                request.getActive());
    }

    public BoardResponse getBoard(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("not found board"));

        return new BoardResponse(
                findBoard.getId(),
                findBoard.getName(),
                findBoard.getDescription(),
                findBoard.getActive(),
                findBoard.getCreatedAt(),
                findBoard.getUpdatedAt()
        );
    }

    public Page<BoardResponse> searchBoards(BoardSearchCondition condition, Pageable pageable){
        return boardRepository.searchBoardPage(condition, pageable);
    }

    @Transactional
    public void deleteById(Long boardId){
        boardRepository.deleteById(boardId);
    }
}
