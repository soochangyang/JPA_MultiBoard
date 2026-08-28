package scyang.mutilboard.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scyang.mutilboard.domain.board.dto.BoardRequest;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;
import scyang.mutilboard.domain.board.repository.BoardRepository;
import scyang.mutilboard.domain.board.service.BoardService;
import scyang.mutilboard.global.common.ApiResponse;
import scyang.mutilboard.global.common.MessageUtil;
import scyang.mutilboard.global.common.PageResponse;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createBoard(
            @RequestBody @Valid BoardRequest.Create request){
        Long boardId = boardService.createBoard(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(boardId, MessageUtil.getMessage("board.create.success")));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody @Valid BoardRequest.Update request){
        boardService.updateBoard(boardId, request);

        return ResponseEntity
                .ok(ApiResponse.success(null));

    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(
            @PathVariable("boardId") Long boardId){
        BoardResponse board = boardService.getBoard(boardId);

        return ResponseEntity
                .ok(ApiResponse.success(board));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BoardRepository>>> getBoardList(
            @ModelAttribute BoardSearchCondition condition,
            @PageableDefault(size = 10) Pageable pageable){
        Page<BoardResponse> boardResponsePage = boardService.searchBoards(condition, pageable);
        PageResponse pageResponse = new PageResponse(boardResponsePage);

        return ResponseEntity
                .ok(ApiResponse.success(pageResponse));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable("boardId") Long boardId){
        boardService.deleteById(boardId);

        return ResponseEntity.ok(ApiResponse.success());
    }

}
