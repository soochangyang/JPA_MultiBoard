package scyang.mutilboard.domain.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.auth.dto.TokenResponse;
import scyang.mutilboard.domain.auth.service.AuthService;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.common.MessageUtil;
import tools.jackson.databind.ObjectMapper;


import java.lang.reflect.Field;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService; // 컨트롤러가 의존하는 서비스 가짜 객체

    @MockitoBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        MessageSource mockMessageSource = mock(MessageSource.class);

        // given() 대신 lenient().when(...).thenReturn(...) 을 사용합니다!
        lenient().when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("테스트 에러 메시지");

        ReflectionTestUtils.setField(MessageUtil.class, "messageSource", mockMessageSource);
    }

    @Test
    void signUp_success() throws Exception {
        //given
        AuthRequest.SignUp request = new AuthRequest.SignUp("my@company.com", "12345", "Calvin");

        given(authService.signUp(any(AuthRequest.SignUp.class))).willReturn(1L);

        //when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1L))
                .andDo(print());
    }

    @Test
    void login_success() throws Exception {
        //given
        AuthRequest.Login request = new AuthRequest.Login("my@Company.com", "12345");

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

        given(authService.login(any(AuthRequest.Login.class))).willReturn(tokenResponse);

        //when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken))
                .andDo(print());
    }

    @Test
    void logout_success() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    void resetPassword_success() throws Exception {
        // You must provide a valid email format because Spring Validation checks it before invoking any other processes.
        AuthRequest.ResetPassword request = new AuthRequest.ResetPassword("1@company.com", "oldPassword");

        Member mockMember = Member.builder()
                .email("1@company.com")
                .password("oldPassword")
                .build();
        setId(mockMember, 1L);

        //given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));
        doNothing().when(authService).resetPassword(any(AuthRequest.ResetPassword.class));

        //when & then
        mockMvc.perform(put("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }


    private void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}