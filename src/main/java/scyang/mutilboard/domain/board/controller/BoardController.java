package scyang.mutilboard.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import scyang.mutilboard.domain.board.dto.BoardRequest;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;
import scyang.mutilboard.domain.board.service.BoardService;
import scyang.mutilboard.global.common.ApiPageResponse;
import scyang.mutilboard.global.common.ApiResponse;

import static scyang.mutilboard.global.common.MessageUtil.getMessage;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    @PostMapping
    public ApiResponse<Long> createBoard(
            @RequestBody @Valid BoardRequest.Create request){
        Long boardId = boardService.createBoard(request);

        return ApiResponse.created(boardId, getMessage("board.create.success"));
    }

    @PutMapping("/{boardId}")
    public ApiResponse<Void> updateBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody @Valid BoardRequest.Update request){
        boardService.updateBoard(boardId, request);

        return ApiResponse.success(null, getMessage("board.update.success"));
    }

    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse> getBoard(
            @PathVariable("boardId") Long boardId){
        BoardResponse board = boardService.getBoard(boardId);

        return ApiResponse.success(board, getMessage("board.search.success"));
    }

    @GetMapping
    public ApiPageResponse<BoardResponse> getBoardList(
            @ModelAttribute BoardSearchCondition condition,
            @PageableDefault(size = 10) Pageable pageable){
        Page<BoardResponse> boardResponsePage = boardService.searchBoards(condition, pageable);

        return new ApiPageResponse(boardResponsePage, getMessage("board.search.success"));
    }

    @DeleteMapping("/{boardId}")
    public ApiResponse<Void> deleteBoard(@PathVariable("boardId") Long boardId){
        boardService.deleteById(boardId);

        return ApiResponse.success(null, getMessage("board.delete.success"));
    }

}
