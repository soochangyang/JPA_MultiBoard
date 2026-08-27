package scyang.mutilboard.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scyang.mutilboard.domain.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> , BoardRepositoryCustom{
}
