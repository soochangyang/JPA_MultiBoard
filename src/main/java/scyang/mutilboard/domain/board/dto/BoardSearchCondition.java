package scyang.mutilboard.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardSearchCondition {
    private String name;

    public BoardSearchCondition(String name) {
        this.name = name;
    }
}
