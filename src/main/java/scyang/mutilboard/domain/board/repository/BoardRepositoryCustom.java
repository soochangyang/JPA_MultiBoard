package scyang.mutilboard.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;

public interface BoardRepositoryCustom {
    Page<BoardResponse> searchBoardPage(BoardSearchCondition condition, Pageable pageable);
}
