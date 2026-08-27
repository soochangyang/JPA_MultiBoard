package scyang.mutilboard.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scyang.mutilboard.global.common.BaseEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Builder
    public Board(String name, String description, Boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Boolean active = true;

    public void updateInfo(String name, String description, Boolean isActive){
        this.name = name;
        this.description = description;
        this.active = isActive;
    }
}
