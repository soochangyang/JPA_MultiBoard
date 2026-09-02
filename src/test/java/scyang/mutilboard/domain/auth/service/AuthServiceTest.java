package scyang.mutilboard.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.auth.dto.TokenResponse;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.common.MessageUtil;
import scyang.mutilboard.global.jwt.JwtTokenProvider;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private String email1 = "king@world.com";
    private String pwd = "123!@#";
    private String name = "Chris";

    private final static String EMAIL2 = "king@world.com";

    @BeforeEach
    void before() {
/*        // 1. 가짜(Mock) MessageSource 껍데기 만들기
        MessageSource mockMessageSource = mock(MessageSource.class);

        // 2. 가짜 객체에게 "누가 에러 메시지 달라고 하면 아무 문자열이나 뱉어!" 라고 대본(Stubbing) 주기
        given(mockMessageSource.getMessage(any(), any(), any())).willReturn("테스트 에러 메시지");

        // 3. MessageUtil 클래스의 static 필드("messageSource")에 가짜 객체를 강제로 주입!
        ReflectionTestUtils.setField(MessageUtil.class, "messageSource", mockMessageSource);*/
        MessageSource mockMessageSource = mock(MessageSource.class);

        // 🌟 given() 대신 lenient().when(...).thenReturn(...) 을 사용합니다!
        lenient().when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("테스트 에러 메시지");

        ReflectionTestUtils.setField(MessageUtil.class, "messageSource", mockMessageSource);
    }

    @Test
    void signUp_success() {
        //given
        AuthRequest.SignUp request = new AuthRequest.SignUp(email1, pwd, name);

        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        Member newMember = Member.builder()
                .email(request.getEmail())
                .password("1212121212")
                .nickname(request.getNickname())
                .build();

        setId(newMember, 1L);

        given(memberRepository.save(any(Member.class))).willReturn(newMember);

        //when
        Long id = authService.signUp(request);

        //then
        log.info("id: {}", id);

        assertThat(id).isNotNull();
    }

    @Test
    void signUp_fail() {
        //given
        AuthRequest.Login request = new AuthRequest.Login(EMAIL2, pwd);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MessageUtil.getMessage("login.fail"));
    }

    @Test
    void login_success() {
        //given
        AuthRequest.Login request = new AuthRequest.Login(email1, pwd);

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodePassword")
                .build();
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.createToken(anyString(), anyString())).willReturn("myAccessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("myRefreshToken");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        //when
        TokenResponse tokenResponse = authService.login(request);

        //then
        assertThat(tokenResponse.getAccessToken()).isEqualTo("myAccessToken");
        assertThat(tokenResponse.getRefreshToken()).isEqualTo("myRefreshToken");
    }

    @Test
    void resetPassword_success() {
        AuthRequest.ResetPassword request = new AuthRequest.ResetPassword(email1, pwd);

        Member mockMember = Member.builder()
                .email(request.getEmail())
                .nickname("Chris")
                .password("aefafaafaf")
                .build();
        setId(mockMember, 1L);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));

        authService.resetPassword(request);

        //then
        assertThat(mockMember.getPassword()).isNotEqualTo(pwd);
        assertThat(passwordEncoder.matches(request.getNewPassword(), mockMember.getPassword())).isTrue();
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