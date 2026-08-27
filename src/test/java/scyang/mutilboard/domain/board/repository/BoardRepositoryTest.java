package scyang.mutilboard.domain.board.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.dto.BoardSearchCondition;
import scyang.mutilboard.domain.board.entity.Board;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void saveAndFindBoard_success(){
        //given
        Board board = Board.builder()
                .name("Notic Board")
                .description("Stay updated with the latest announcements, news, and important updates for our members.")
                .active(true)
                .build();
        //when
        Board savedBoard = boardRepository.save(board);

        em.flush();
        em.clear();

        Optional<Board> foundBoard = boardRepository.findById(savedBoard.getId());

        assertThat(foundBoard).isPresent();
        assertThat(foundBoard.orElseThrow().getName()).isEqualTo(board.getName());
        assertThat(foundBoard.orElseThrow().getDescription()).isEqualTo(board.getDescription());
        assertThat(foundBoard.orElseThrow().getActive()).isEqualTo(board.getActive());
    }

    @Test
    void updateBoard_success(){
        //given
        Board board = Board.builder()
                .name("Old Name")
                .description("Olded Description")
                .active(true)
                .build();

        Board savedBoard = boardRepository.save(board);

        em.flush();
        em.clear();

        //when
        Board foundBoard = boardRepository.findById(savedBoard.getId()).orElseThrow();
        foundBoard.updateInfo("New Board Name", "Update new description", false);

        em.flush();
        em.clear();

        //then
        Board updatedBoard = boardRepository.findById(savedBoard.getId()).orElseThrow();
        assertThat(updatedBoard.getName()).isEqualTo("New Board Name");
        assertThat(updatedBoard.getDescription()).isEqualTo("Update new description");
        assertThat(updatedBoard.getActive()).isEqualTo(false);
    }

    @Test
    void searchBoardPage_success(){
        //given
        for (int i = 0; i < 50; i++) {
            Board board = Board.builder()
                    .name("Free Board "+i)
                    .description("Content "+i)
                    .active(true)
                    .build();
            boardRepository.save(board);
        }

        em.flush();
        em.clear();

        BoardSearchCondition condition = new BoardSearchCondition("Free");
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<BoardResponse> result = boardRepository.searchBoardPage(condition, pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(50);
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getContent()).extracting("name").contains("Free Board 0", "Free Board 1",
                "Free Board 2","Free Board 3","Free Board 4","Free Board 5",
                "Free Board 6","Free Board 7","Free Board 8","Free Board 9");

    }


    @Test
    void deleteBoard_success(){
        //given
        Board board = Board.builder()
                .name("Delete target")
                .description(" to be delete")
                .active(true)
                .build();

        Board savedBoard = boardRepository.save(board);
        em.flush();
        em.clear();

        //when
        boardRepository.deleteById(savedBoard.getId());
        em.flush();
        em.clear();

        //then
        Optional<Board> foundBoard = boardRepository.findById(savedBoard.getId());
        assertThatThrownBy(() -> foundBoard.orElseThrow()).isInstanceOf(NoSuchElementException.class);

    }

}
