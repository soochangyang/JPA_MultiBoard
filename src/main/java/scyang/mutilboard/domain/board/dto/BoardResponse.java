package scyang.mutilboard.domain.board.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardResponse {

    private Long boardId;
    private String name;
    private String description;
    private Boolean active = true;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public BoardResponse(Long boardId, String name, String description,
                         Boolean active, LocalDateTime createAt, LocalDateTime updatedAt) {
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createAt = createAt;
        this.updatedAt = updatedAt;
    }

    public BoardResponse(Long boardId, String name, String description, Boolean active) {
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.active = active;
    }
}
