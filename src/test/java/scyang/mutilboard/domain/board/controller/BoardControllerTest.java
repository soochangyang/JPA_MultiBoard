package scyang.mutilboard.domain.board.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import scyang.mutilboard.domain.board.dto.BoardRequest;
import scyang.mutilboard.domain.board.dto.BoardResponse;
import scyang.mutilboard.domain.board.service.BoardService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    @Test
    void createBoard_success() throws Exception{
        //given
        BoardRequest.Create request = new BoardRequest.Create("자유게시판", "자유롭게 글을 쓰는 공간", true);

        given(boardService.createBoard(any(BoardRequest.Create.class))).willReturn(1L);

        String jsonRequest = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(post("/api/boards") // POST /api/boards 주소로 요청
                        .contentType(MediaType.APPLICATION_JSON) // "나 JSON 보낸다!"
                        .content(jsonRequest)) // JSON 데이터 본문 실어 보내기
                .andExpect(status().isCreated()) // 🌟 검증 1: HTTP 상태 코드가 201(OK)인가?
                .andExpect((ResultMatcher) jsonPath("$.success").value(true)) // 🌟 검증 2: 공통 응답 ApiResponse의 success가 true인가?
                .andExpect((ResultMatcher) jsonPath("$.data").value(1)) // 🌟 검증 3: 반환된 ID 데이터가 1인가?
                .andDo(print()); // 🌟 보너스: 테스트 실행 동안 주고받은 HTTP 콘솔 로그 출력하기
    }


    @Test
    void updateBoard_success() throws Exception {
        //given
        Long boardId = 1L;
        BoardRequest.Update request = new BoardRequest.Update("분당 지역 게시판", "분당 지역 회원들의 게시판입니다. ", true);
        String jsonRequest = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(put("/api/boards/{boardId}", boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    void getBoard_success() throws Exception {
        //given
        Long boardId = 1L;

        BoardResponse response = new BoardResponse(boardId, "자유게시판", "자유롭게 작성해주세요", true);

        given(boardService.getBoard(boardId)).willReturn(response);

        //when & then
        mockMvc.perform(get("/api/boards/{boardId}", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("자유게시판"))
                .andDo(print());
    }

    @Test
    void getBoardList_success() throws Exception{
        //given
        BoardResponse response = new BoardResponse(1L, "자유게시판", "자유롭게 작성해주세요", true);
        Page<BoardResponse> pageResponse = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(boardService.searchBoards(any(), any())).willReturn(pageResponse);

        mockMvc.perform(get("/api/boards")
                .param("page", "0")
                .param("size", "10")
                .param("name", "자유게시판"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("자유게시판"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andDo(print());
    }

    @Test
    void deleteBoard_success() throws Exception{
        //given
        Long boardId = 1L;

        //when & then
        mockMvc.perform(delete("/api/boards/{boardId}", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());
    }

}