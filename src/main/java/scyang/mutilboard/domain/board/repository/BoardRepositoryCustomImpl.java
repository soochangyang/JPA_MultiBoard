package scyang.mutilboard.domain.board.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;
import scyang.mutilboard.domain.board.dto.QBoardResponse;
import scyang.mutilboard.domain.board.entity.Board;
import scyang.mutilboard.global.common.Querydsl4RepositorySupport;

import static scyang.mutilboard.domain.board.entity.QBoard.board;

@Repository
public class BoardRepositoryCustomImpl extends Querydsl4RepositorySupport implements BoardRepositoryCustom {

    public BoardRepositoryCustomImpl() {
        super(Board.class);
    }

    @Override
    public Page<BoardResponse> searchBoardPage(BoardSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                queryFactory -> queryFactory
                            .select(new QBoardResponse(
                                    board.id,
                                    board.name,
                                    board.description,
                                    board.active,
                                    board.createdAt,
                                    board.updatedAt
                            ))
                            .from(board)
                            .where(boardNameContains(condition.getName())),
                queryFactory -> queryFactory
                            .select(board.count())
                            .from(board)
                            .where(boardNameContains(condition.getName()))
                );
    }

    private BooleanExpression boardNameContains(String name){
        return StringUtils.hasText(name) ? board.name.contains(name) : null;
    }
}
