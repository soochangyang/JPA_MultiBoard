package scyang.mutilboard.domain.board.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scyang.mutilboard.domain.board.entity.Board;

public class BoardRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        @NotBlank(message = "board name is required")
        private String name;

        private String description;

        private Boolean active = true;

        public Board toEntity(){
            return Board.builder()
                    .name(this.name)
                    .description(this.description)
                    .active(this.active)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update{
        private String name;

        private String description;

        private Boolean active = true;
    }
}
